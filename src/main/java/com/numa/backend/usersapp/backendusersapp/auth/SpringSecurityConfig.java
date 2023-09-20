package com.numa.backend.usersapp.backendusersapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.numa.backend.usersapp.backendusersapp.auth.filters.JwtAuthenticationFilter;
import com.numa.backend.usersapp.backendusersapp.auth.filters.JwtValidationFilter;

@Configuration
public class SpringSecurityConfig {
	
	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();   							// Este es un hashing para encriptar contrase;as muy robusto y permite generar varias encriptaciones para un mismo string
	}
	
	@Bean
	AuthenticationManager authenticationManager() throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
		
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		return http.authorizeHttpRequests()
		.requestMatchers(HttpMethod.GET, "/users").permitAll()
		.requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole("USER","ADMIN")
		.requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
		.requestMatchers(HttpMethod.DELETE, "/users{id}").hasRole("ADMIN")
		.requestMatchers(HttpMethod.PUT, "/users").hasRole("ADMIN")
		.anyRequest().authenticated()
		.and()
		.addFilter(new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))
		.addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
		.csrf(config -> config.disable()).
		sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.build();
		
	}

}
