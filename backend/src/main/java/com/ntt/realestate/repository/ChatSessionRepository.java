package com.ntt.realestate.repository;

import com.ntt.realestate.model.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);
    List<ChatSession> findByUserIdAndUpdatedAtGreaterThanEqualOrderByUpdatedAtDesc(String userId, Instant since);
    Optional<ChatSession> findByIdAndUserId(String id, String userId);
}
