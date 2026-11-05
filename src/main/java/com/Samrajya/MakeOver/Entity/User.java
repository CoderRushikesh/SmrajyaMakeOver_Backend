package com.Samrajya.MakeOver.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import lombok.Data;

@Data
@Entity
@Table(name = "users") // "user" is a reserved word in PostgreSQL
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

	 // USER or ADMIN (null on old rows = USER)
	 @Column(length = 20)
	 private String role;

	 private LocalDateTime createdAt;

	 @PrePersist
	 void onCreate() {
		 if (createdAt == null) createdAt = LocalDateTime.now(ZoneOffset.UTC);
		 if (role == null) role = "USER";
	 }
	
}
