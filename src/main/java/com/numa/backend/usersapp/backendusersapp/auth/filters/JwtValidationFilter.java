package com.numa.backend.usersapp.backendusersapp.auth.filters;

import static com.numa.backend.usersapp.backendusersapp.auth.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numa.backend.usersapp.backendusersapp.auth.SimpleGrantedAuthorityJsonCreator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter{

	public JwtValidationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String header= request.getHeader(HEADER_AUTHORIZATION);
		
		if (header==null || !header.startsWith(PREFIX_TOKEN)) {
			chain.doFilter(request, response);
			return;
		}
		
		String token=header.replace(PREFIX_TOKEN , "");
		
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build()
			.parseClaimsJws(token).getBody();
			
			Object authoritiesClaims = claims.get("authorities");
			
			Collection<? extends GrantedAuthority> authorities = Arrays
					.asList(new ObjectMapper()						
							.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
							.readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));
			
			String username = claims.getSubject();
			
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication); 			// Con este nos autenticamos y dejamos pasar al usuario a este recurso protegido
			chain.doFilter(request, response);
			
		} catch(JwtException e){
			Map<String, String> body = new HashMap<>();
			
			body.put("error", e.getMessage());
			body.put("message", "El token JWT no es valido");
			response.getWriter().write(new ObjectMapper().writeValueAsString(body));
			response.setStatus(403);
			response.setContentType("application/json");
			
		}
		
	}

	
	
}
