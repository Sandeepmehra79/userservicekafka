package com.userService.demo.security;


//@Configuration
//public class WebSecurity {
//    @Order(1)
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests((request) -> {
//                                request.requestMatchers("/user/**").permitAll();
//                                request.requestMatchers("/role/**").permitAll();
//                                request.requestMatchers("/auth/**").permitAll();
//                                request.requestMatchers("/role/home").hasAuthority("ADMIN");
//
//                    request.requestMatchers("/home/user").hasAuthority("USER");
//
//                    request.requestMatchers("/home/admin").hasAuthority("ADMIN");
//
//                    request.requestMatchers("/home/support").hasAuthority("SUPPORT");
//
//                    request.requestMatchers("/home/all").hasAnyAuthority("ADMIN","USER", "SUPPORT");
//
//                    request.anyRequest().authenticated();
//                        })
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())
//                .build();
//    }
//
//
//}
