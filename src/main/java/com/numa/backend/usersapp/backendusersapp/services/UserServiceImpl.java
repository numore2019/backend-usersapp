package com.numa.backend.usersapp.backendusersapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.numa.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.numa.backend.usersapp.backendusersapp.models.dto.mapper.DtoMapperUser;
import com.numa.backend.usersapp.backendusersapp.models.entities.Role;
import com.numa.backend.usersapp.backendusersapp.models.entities.User;
import com.numa.backend.usersapp.backendusersapp.repositories.RoleRepository;
import com.numa.backend.usersapp.backendusersapp.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired		// Esa es la idea de los service interactuar con diferentes repositories en la misma transaccion como con roleRepository
    private UserRepository repository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = (List<User>) repository.findAll();
        return users.stream().map(u -> DtoMapperUser.builder().setUser(u).build()) // Con stream ingresamos datos de un tipo, cambiamos a otro y los pasamos a una lista 
        		.collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return repository.findById(id)										// Buscamos por el id y lo modificamos con el map 
        		.map(u -> DtoMapperUser.builder().setUser(u).build());		//Si objeto u esta presente entonces lo transforma a un DtoMapperUser pasando el mismo objeto u como parametro
    }

    @Override
    @Transactional
    public UserDto save(User user) {
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	
    	Optional<Role> o = roleRepository.findByName("ROLE_USER");
    	List<Role> roles= new ArrayList<>();
    	if (o.isPresent()) {
        	roles.add(o.orElseThrow());	
		}
    	user.setRoles(roles);
        return DtoMapperUser.builder().setUser(repository.save(user)).build();
    }

    @Override
    @Transactional
    public Optional<UserDto> update(User user, Long id) {
        Optional<User> o = repository.findById(id);
        User userOptional = null;
        if (o.isPresent()) {
            User userDb = o.orElseThrow();
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            userOptional = repository.save(userDb);
        }
        return Optional.ofNullable(DtoMapperUser.builder().setUser(userOptional).build());
    }

    @Override
    @Transactional
    public void remove(Long id) {
        repository.deleteById(id);
    }
    
}
