package com.Samrajya.MakeOver.Controller;

//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Samrajya.MakeOver.Entity.User;
import com.Samrajya.MakeOver.Interfaces.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
public class TestController {

	private final UserRepository userRepository;
	
	@PostMapping("/test")
	public String test(@RequestBody User user) {
		
	
		userRepository.save(user);
		log.info(user.toString());
		return "Hello World! " + user;
	}
}
