package com.demo.uaa.uaaserver;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface Repository extends MongoRepository<UaaModel, String> {

}
