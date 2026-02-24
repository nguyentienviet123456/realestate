package com.ntt.realestate.service;

import com.ntt.realestate.model.ChatMessage;
import com.ntt.realestate.model.ChatSession;
import com.ntt.realestate.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final LlmService llmService;

    public ChatSession sendMessage(String sessionId, String userId, String content) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (session.getMessages() == null) {
            session.setMessages(new ArrayList<>());
        }

        Instant now = Instant.now();

        // Add user message
        session.getMessages().add(ChatMessage.builder()
            .role("user").type("text").content(content).timestamp(now).build());

        // Send to LLM and get response
        List<ChatMessage> chatHistory = session.getMessages();
        String aiResponse;
        try {
            aiResponse = llmService.sendChatMessage(content, chatHistory);
        } catch (Exception e) {
            log.error("Failed to get LLM response for session={}: {}", sessionId, e.getMessage());
            aiResponse = "申し訳ございません。現在応答を取得できません。もう一度お試しください。";
        }

        session.getMessages().add(ChatMessage.builder()
            .role("assistant").type("text").content(aiResponse).timestamp(Instant.now()).build());

        session.setUpdatedAt(Instant.now());
        return chatSessionRepository.save(session);
    }
}
