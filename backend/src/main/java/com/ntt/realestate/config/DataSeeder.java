package com.ntt.realestate.config;

import com.ntt.realestate.model.ChatSession;
import com.ntt.realestate.model.User;
import com.ntt.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) {
        seedUser("admin", "admin123", "ROLE_ADMIN", "Administrator");
        seedUser("user1", "abcxyz@1", "ROLE_USER", "User1");
        seedUser("user2", "abc123@2", "ROLE_USER", "User2");

        backfillOrphanedSessions();
    }

    private void seedUser(String username, String rawPassword, String role, String displayName) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .displayName(displayName)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
            userRepository.save(user);
            log.info("Seeded user: {}", username);
        }
    }

    private void backfillOrphanedSessions() {
        User adminUser = userRepository.findByUsername("admin").orElse(null);
        if (adminUser == null) {
            return;
        }

        var result = mongoTemplate.updateMulti(
            Query.query(Criteria.where("userId").exists(false)),
            Update.update("userId", adminUser.getId()),
            ChatSession.class
        );
        if (result.getModifiedCount() > 0) {
            log.info("Backfilled {} orphaned sessions with userId={}", result.getModifiedCount(), adminUser.getId());
        }
    }
}
