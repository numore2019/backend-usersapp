package com.numa.backend.usersapp.backendusersapp.models.dto.mapper;

import com.numa.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.numa.backend.usersapp.backendusersapp.models.entities.User;

public class DtoMapperUser {
	
	private User user;
	
	private DtoMapperUser() {
	}
	
	public static DtoMapperUser builder() {
		return new DtoMapperUser();
	}

	public DtoMapperUser setUser(User user) {
		this.user = user;
		return this;
	}
	
	public UserDto build() {
		
		if (user==null) {
			throw new RuntimeException("Debe pasar entity user ");
		}
		
		UserDto userDto = new UserDto(this.user.getId(), user.getUsername(), user.getEmail());
		return userDto;
	}
	
	
}
