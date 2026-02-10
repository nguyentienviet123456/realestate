package com.ntt.realestate.controller;

import com.ntt.realestate.dto.ChatSessionSummary;
import com.ntt.realestate.model.ChatSession;
import com.ntt.realestate.model.PropertyDetails;
import com.ntt.realestate.model.User;
import com.ntt.realestate.repository.ChatSessionRepository;
import com.ntt.realestate.repository.PropertyDetailsRepository;
import com.ntt.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ChatSessionController {

    private final ChatSessionRepository chatSessionRepository;
    private final PropertyDetailsRepository propertyDetailsRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ChatSessionSummary>> listSessions() {
        String userId = getCurrentUserId();
        List<ChatSessionSummary> sessions = chatSessionRepository
            .findByUserIdOrderByUpdatedAtDesc(userId)
            .stream()
            .map(s -> ChatSessionSummary.builder()
                .id(s.getId())
                .title(s.getTitle())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatSession> getSession(@PathVariable String id) {
        String userId = getCurrentUserId();
        return chatSessionRepository.findByIdAndUserId(id, userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/property")
    public ResponseEntity<PropertyDetails> getPropertyDetails(@PathVariable String id) {
        String userId = getCurrentUserId();
        return chatSessionRepository.findByIdAndUserId(id, userId)
            .flatMap(session -> propertyDetailsRepository.findBySessionId(session.getId()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private String getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getId();
    }
}
