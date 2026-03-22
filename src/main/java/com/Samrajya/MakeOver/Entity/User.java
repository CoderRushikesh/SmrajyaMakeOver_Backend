package com.Samrajya.MakeOver.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class User {

	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 private String name;
	 
	 @Column(unique = true , nullable = false) 
	 private String email;
	 private String password;
	 private String mobile;
	 private String address;
	 private String city;
	
}
