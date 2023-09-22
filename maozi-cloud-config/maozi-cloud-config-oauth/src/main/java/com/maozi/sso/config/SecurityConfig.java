//package com.maozi.sso.config;
//import static org.springframework.security.config.Customizer.withDefaults;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@EnableWebSecurity
//@Configuration(proxyBeanMethods = false)
//public class SecurityConfig {
//
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		
//		http
//			.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
//			
//			.oauth2Login(oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/demo-client-oidc"))
//			
//			.oauth2Client(withDefaults());
//		
//		return http.build();
//		
//	}
//
//}