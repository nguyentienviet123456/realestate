package com.ntt.realestate.service;

import com.ntt.realestate.model.ChatMessage;
import com.ntt.realestate.model.ChatSession;
import com.ntt.realestate.model.PropertyDetails;
import com.ntt.realestate.model.PropertyField;
import com.ntt.realestate.repository.ChatSessionRepository;
import com.ntt.realestate.repository.PropertyDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving property extraction results from LLM callback.
 * Used by both PropertyCallbackController (real LLM) and MockLlmService (dev mode).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyCallbackService {

    private final ChatSessionRepository chatSessionRepository;
    private final PropertyDetailsRepository propertyDetailsRepository;

    public void processCallback(String sessionId, String originalFileName, List<PropertyField> fields) {
        ChatSession session = chatSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        Instant now = Instant.now();

        long doneCount = fields.stream().filter(f -> "done".equals(f.getStatus())).count();
        long pendingCount = fields.stream().filter(f -> "pending".equals(f.getStatus())).count();
        String summary = String.format(
            "物件情報を含むファイルを読み取り、整理して出力しました。\n" +
            "抽出結果：全%d件（完了 %d件・確認中 %d件）",
            doneCount + pendingCount, doneCount, pendingCount
        );

        if (session.getMessages() == null) {
            session.setMessages(new ArrayList<>());
        }
        session.getMessages().add(ChatMessage.builder()
            .role("assistant").type("analysis_result")
            .content(summary)
            .timestamp(now).build());
        session.setUpdatedAt(now);

        PropertyDetails details = PropertyDetails.builder()
            .sessionId(sessionId)
            .originalFileName(originalFileName)
            .fields(fields)
            .createdAt(now)
            .updatedAt(now)
            .build();
        details = propertyDetailsRepository.save(details);

        session.setPropertyDetailsId(details.getId());
        chatSessionRepository.save(session);

        log.info("Saved property details for session {} — {} fields", sessionId, fields.size());
    }
}
