package com.numa.backend.usersapp.backendusersapp.models.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;

    @Column(unique = true)
    private String email;

    @ManyToMany																		//Un rol puede estar asociado a muchos usuarios pero un usuario puede tener muchos roles tambien
	@JoinTable(name="user_roles", joinColumns = @JoinColumn(name="user_id"), 		//Los nombres de llave en singular "user_id"
	inverseJoinColumns = @JoinColumn(name="role_id"),								// La tabla va a tener 2 llaves foraneas "user_id" y "role_id"
	uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","role_id"})}) 	//Esta configuracion indica que las llaves tienen que ser unicas o irrepetibles
    private List<Role> roles;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

    

}
