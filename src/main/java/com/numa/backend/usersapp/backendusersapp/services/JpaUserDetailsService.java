package com.numa.backend.usersapp.backendusersapp.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.numa.backend.usersapp.backendusersapp.models.entities.User;
import com.numa.backend.usersapp.backendusersapp.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository repository;
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<User> o = repository.getUserByUsername(username);
		
		if (!o.isPresent()) {										// Valida si no esta presente 
			throw new UsernameNotFoundException("El Username no existe en el sistema!");
		}
		User user = o.orElseThrow(); 								// Obtenemos el user desde la base de datos mediante Optional
		
		List<GrantedAuthority> authorities = user.getRoles()		 //Obtenemos los roles
				.stream()											//Invocamos la Api stream y despues el map para modificar
				.map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList()); 
																	// Por cada role creamos stream con instancias del tipo SimpleGrantedAuthority pasando el nombre del rol al constructor
																	// Este user es con toda la ruta porque no es el mismo tipo de user del modelo sino que es de security
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
	}
																	// Todos los roles en Spring security empiezan con el prefijo ROLE_ y siempre van en mayuscula
}
