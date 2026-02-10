package com.ntt.realestate.repository;

import com.ntt.realestate.model.PropertyDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PropertyDetailsRepository extends MongoRepository<PropertyDetails, String> {
    Optional<PropertyDetails> findBySessionId(String sessionId);
}
