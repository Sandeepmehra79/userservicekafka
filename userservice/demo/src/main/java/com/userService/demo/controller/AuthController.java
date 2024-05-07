package com.userService.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userService.demo.dto.JwtData;
import com.userService.demo.dto.UserDto.*;
import com.userService.demo.exception.UserNotFoundException;
import com.userService.demo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginRequestDto request) throws UserNotFoundException {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        authService.logout(request.getToken(), request.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody SignUpRequestDto request) throws JsonProcessingException {
        UserResponseDto userResponseDto = authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

//    @PostMapping("/validate")
//    public ResponseEntity<JwtData> validateToken(@RequestBody ValidateTokenRequestDto request) throws Exception {
//        return ResponseEntity.ok(authService.validate(request.getToken(), request.getUserId()));
//    }

    @PostMapping("/validate")
    public ResponseEntity<JwtData> validateToken(@RequestBody ValidateTokenRequestDto request) throws Exception {
        JwtData jwtData = new JwtData();
        jwtData.setEmail(request.getToken());
        return ResponseEntity.ok(jwtData);
    }
}
