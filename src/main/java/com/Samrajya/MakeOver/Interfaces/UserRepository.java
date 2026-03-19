package com.Samrajya.MakeOver.Interfaces;

import com.Samrajya.MakeOver.Entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;




public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

	 
}

