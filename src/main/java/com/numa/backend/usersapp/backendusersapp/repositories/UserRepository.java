package com.numa.backend.usersapp.backendusersapp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.numa.backend.usersapp.backendusersapp.models.entities.User;


public interface UserRepository
        extends CrudRepository<User, Long> {
	
	Optional<User> findByUsername(String username);

	
	@Query("select u from User u where u.username=?1") 							// Esta es una consulta de hibernate de JPA es una consulta llamada JQL o HQL HIbernate Query Language 
	Optional<User> getUserByUsername(String username);		            //Aca hacemos un select pero al obeto Usuario en si donde el ?1 corresponde a el primer parametro que viene en el metodo
	
}
