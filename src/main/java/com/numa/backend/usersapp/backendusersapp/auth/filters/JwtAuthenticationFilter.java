package com.numa.backend.usersapp.backendusersapp.auth.filters;

import static com.numa.backend.usersapp.backendusersapp.auth.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numa.backend.usersapp.backendusersapp.models.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
																									
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter { 			//Maneja por debajo una ruta URL llamada login y se ejecuta como interceptor solo cuando el metodo request sea post y la ruta login
	
	
	private AuthenticationManager authenticationManager;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager=authenticationManager;
	}

	@Override 											
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)  // Esta es para intentar el login donde request posea los datos del username y password y lo leemos con readValue
			throws AuthenticationException {
		
		User user=null;
		String username =null;
		String password = null;
		
		try {
			user= new ObjectMapper().readValue(request.getInputStream(), User.class);
			username=user.getUsername();
			password = user.getPassword();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authToken); 								 		// Aca hace el proceso de autenticar con el metodo Authenticate pasando AuthToken como parametro
	}

	@Override 																						// Si salio bien la autenticacion devolver el token JWT o generar el token con los datos del login para el front
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
                .getUsername();

        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        boolean isAdmin = roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        Claims claims = Jwts.claims();
        claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
        claims.put("isAdmin", isAdmin);
        claims.put("username", username);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .signWith(SECRET_KEY)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message", String.format("Hola %s, has iniciado sesion con exito!", username));
        body.put("username", username);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

	@Override 																		
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, //Si salio mal devolver una respuesta 404 con msj de error
			AuthenticationException failed) throws IOException, ServletException {

		Map<String, Object> body = new HashMap<>();
		body.put("message", "Error al autenticarse con las credenciales!");
		body.put("error", failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}

	
	
}
