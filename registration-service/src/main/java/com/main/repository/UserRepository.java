package com.main.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.main.modal.UserInformation;

public interface UserRepository extends MongoRepository<UserInformation, String> {

}
