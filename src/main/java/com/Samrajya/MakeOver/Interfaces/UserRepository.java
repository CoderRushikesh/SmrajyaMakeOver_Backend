package com.Samrajya.MakeOver.Interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Samrajya.MakeOver.Repository.User;

public interface UserRepository extends JpaRepository<User, Long> {

	
}
