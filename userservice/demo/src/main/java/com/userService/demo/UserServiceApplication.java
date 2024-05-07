package com.userService.demo;

import com.userService.demo.security.RSAKeyRecord;
import com.userService.demo.security.models.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.time.Instant;
import java.util.UUID;

//https://docs.spring.io/spring-authorization-server/reference/getting-started.html
//https://docs.spring.io/spring-authorization-server/reference/guides/how-to-jpa.html#implement-core-services
@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {



		SpringApplication.run(UserServiceApplication.class, args);
	}

}
