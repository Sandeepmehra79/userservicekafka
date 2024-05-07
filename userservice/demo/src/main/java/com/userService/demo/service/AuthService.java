package com.userService.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userService.demo.client.KafkaProducerClient;
import com.userService.demo.dto.JwtData;
import com.userService.demo.dto.SendEmailMessageDto;
import com.userService.demo.model.Role;
import com.userService.demo.security.RSAKeyRecord;
import com.userService.demo.dto.UserDto.UserResponseDto;
import com.userService.demo.exception.UserNotFoundException;
import com.userService.demo.mapper.UserToUserResponseDto;
import com.userService.demo.model.Session;
import com.userService.demo.model.SessionStatus;
import com.userService.demo.model.User;
import com.userService.demo.repository.SessionRepository;
import com.userService.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.*;

@Service
public class AuthService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final User user;
    private RSAKeyRecord rsaKeyRecord;
    private KafkaProducerClient kafkaProducerClient;
//    for converting json object to java object and vice versa
    private ObjectMapper objectMapper;

    @Autowired
    public AuthService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository,
                       SessionRepository sessionRepository, RSAKeyRecord rsaKeyRecord, User user,
                       KafkaProducerClient kafkaProducerClient, ObjectMapper objectMapper) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.rsaKeyRecord = rsaKeyRecord;
        this.user = user;
        this.kafkaProducerClient = kafkaProducerClient;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<UserResponseDto> login(String email, String password) throws UserNotFoundException {
        Optional<User>  userOptional = userRepository.getUserByEmail(email);

        // checking if the user exist with the provided email

        if(userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User user = userOptional.get();
//         checking if the password is matching or not


        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            System.out.println(email);
            System.out.println(password);
            System.out.println(user.getPassword());
            throw new BadCredentialsException("Bad credentials");
        }

//        String token = RandomStringUtils.randomAlphanumeric(30);

        Map<String , Object> jwtData = new HashMap<>();

        jwtData.put("email", email);
        jwtData.put("CreatedAt", new Date());
        // adding 30 minutes to the current system time for the expiry
        jwtData.put("ExpiresAt", new Date((System.currentTimeMillis()) + (30*60*1000)));
//        jwtData.put("roles", user.getRoles().stream().map(Role::getName).toList());

        String token = Jwts.builder().claims(jwtData).signWith(rsaKeyRecord.rsaPrivateKey())
                .compact();

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);

        sessionRepository.save(session);

        UserResponseDto userResponseDto = UserToUserResponseDto.convert(user);


        MultiValueMap<String , String > headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token: " + token);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new ResponseEntity<UserResponseDto>(userResponseDto, headers , HttpStatus.OK);
    }

    public void logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository
                .findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);
    }

    public UserResponseDto signUp(String email, String password) throws JsonProcessingException {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);
//         this is for kafka

        SendEmailMessageDto sendEmailMessageDto = new SendEmailMessageDto();
        sendEmailMessageDto.setTo(savedUser.getEmail());
        sendEmailMessageDto.setSubject("sandeepmehra79@gmail.com");
        sendEmailMessageDto.setBody("welcome body");
        sendEmailMessageDto.setSubject("welcome subject");
        System.out.println("this is we are doing ");
        kafkaProducerClient.sendMessage("sendEmail" ,
                objectMapper.writeValueAsString(sendEmailMessageDto));
        return UserToUserResponseDto.convert(savedUser);
    }

    public JwtData validate(String token, Long user_id) {
//        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

//        if (sessionOptional.isEmpty()) {
//            return SessionStatus.ENDED;
//        }
//
//        Session session = sessionOptional.get();
//
//        if (!session.getSessionStatus().equals(SessionStatus.ACTIVE)) {
//            return SessionStatus.ENDED;
//        }

        // lets create a jwtData object from the jwt token
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(rsaKeyRecord.rsaPublicKey())
                .build()
                .parseSignedClaims(token);

        Date expireDate = claimsJws.getPayload().getExpiration();
        String email = claimsJws.getPayload().get("email", String.class);
        Date createdAt = claimsJws.getPayload().getIssuedAt();

        JwtData jwtData = new JwtData();
        // now lets compare the user_id that we have is valid for the token or not
        Optional<User> userOptional = userRepository.getUserById(user_id);
        if(userOptional.isEmpty()) {
            jwtData.setEmail("user not found");
            return jwtData;
        }

        // if the user is valid we need to confirm if the username(here email is same or not) \
        if(!userOptional.get().getEmail().equals(email)) {
            System.out.println(userOptional.get().getEmail());
            System.out.println(email);
            jwtData.setEmail("token is not valid for this use");
            return jwtData;
        }
        // now we have the claims from the jwt token we can create a jwt token object

        jwtData.setEmail(email);
        jwtData.setRoles("JustTestRoles".lines().toList());

//        if(expireDate.before(new Date())) {
//            return SessionStatus.ENDED;
//        }

        return jwtData;
    }
}
