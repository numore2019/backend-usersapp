package com.numa.backend.usersapp.backendusersapp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.numa.backend.usersapp.backendusersapp.models.entities.Role;
import com.numa.backend.usersapp.backendusersapp.models.entities.User;


public interface RoleRepository
        extends CrudRepository<Role, Long> {
	
	Optional<Role> findByName(String name);


}
