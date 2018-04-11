package com.main.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.main.modal.UserInformation;
import com.main.repository.UserRepository;
import com.main.service.UserRegistrationService;

@RestController
public class RegistrationController {

	@Autowired
	UserRegistrationService userService;

	@Autowired
	UserRepository userRepository;

	private String addUserInformation;
	
	@PostMapping("/register")
	public String addUserInformation(@Valid @RequestBody UserInformation dataOfUserInBody) {
		UserInformation info = null;
		String message = "";
		Optional<UserInformation> userFound=userRepository.findById(dataOfUserInBody.getUserId());
		System.out.println(userFound.isPresent());
		
		if (userFound.isPresent()) {
			message = "Already Exists";
		} 
		else {
			addUserInformation = userService.addUserInformation(dataOfUserInBody);
			message = dataOfUserInBody.getUserId()+","+dataOfUserInBody.getRole();
		}	
		return message;
			
	}

	@PostMapping("/login")
	public String getUserInformationn(@RequestBody UserInformation userInfoInBody) {
		String message = "";
		String getPassword = "";
		UserInformation info = null;
		Optional<UserInformation> retrieverIformation = userService.getUserInformation(userInfoInBody.getUserId());

		if (!retrieverIformation.isPresent()) {
			message = "Not Found";
		} else {
			info = retrieverIformation.get();
			getPassword = info.getPassword();
			if (getPassword.equals(userInfoInBody.getPassword())) {
				message = userInfoInBody.getUserId()+","+info.getRole();

			} else {
				message = "Invalid";
			}
		}

		return message;
	}
}
