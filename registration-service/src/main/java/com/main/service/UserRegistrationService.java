package com.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.main.exception.UserNotFountException;
import com.main.modal.UserInformation;
import com.main.repository.UserRepository;

@Service
public class UserRegistrationService {
	
	
	
	@Autowired
	UserRepository userRepository;

	public List<UserInformation> getAllRegisteredUserInformation() {
		List<UserInformation> allUsersFound = new ArrayList<>();
		userRepository.findAll().forEach(allUsersFound::add);
		return allUsersFound;
	}

	public Optional<UserInformation> getUserInformation(String id) {
		 Optional<UserInformation> userFound=userRepository.findById(id);
		 if(!userFound.isPresent()) {
			 throw new UserNotFountException("id-"+id);
		 }
		 return userFound;
	}

	public String addUserInformation(UserInformation dataOfUserInBody) {
		//get user if exists		
		userRepository.save(dataOfUserInBody);
		return "added";

	}

	public void modifyUserInformation(UserInformation dataOfUserInBody) {
		userRepository.save(dataOfUserInBody);

	}

	public void deleteUserInformation(String id) {
		 Optional<UserInformation> userFound=userRepository.findById(id);
		 if(!userFound.isPresent()) {
			 throw new UserNotFountException("id-"+id);
		 }
		 userRepository.deleteById(id);
	}

}
