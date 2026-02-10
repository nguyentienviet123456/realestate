package com.ntt.realestate.service;

import com.ntt.realestate.dto.ExtractResponse;
import com.ntt.realestate.model.ChatMessage;
import com.ntt.realestate.model.ChatSession;
import com.ntt.realestate.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final LlmService llmService;
    private final ChatSessionRepository chatSessionRepository;

    @Value("${app.callback.base-url:http://localhost:8080}")
    private String callbackBaseUrl;

    /**
     * Extract PDF flow:
     * 1. Create/reuse session (sync)
     * 2. Send PDF to LLM API (sync — waits for LLM to acknowledge)
     * 3. LLM processes and calls callback endpoint to save PropertyDetails
     * 4. Return sessionId + status to frontend
     */
    public ExtractResponse extractPdf(MultipartFile file, String sessionId, String userId) throws IOException {
        byte[] pdfBytes = file.getBytes();
        String fileName = file.getOriginalFilename();

        Instant now = Instant.now();

        // Step 1: Create or reuse session
        ChatSession session;
        if (sessionId != null && !sessionId.isBlank()) {
            session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseGet(() -> createNewSession(fileName, now, userId));
        } else {
            session = createNewSession(fileName, now, userId);
        }

        if (session.getMessages() == null) {
            session.setMessages(new ArrayList<>());
        }
        session.getMessages().add(ChatMessage.builder()
            .role("user").type("pdf_upload")
            .content("Uploaded: " + fileName)
            .timestamp(now).build());
        session.setUpdatedAt(now);
        session = chatSessionRepository.save(session);

        // Step 2: Send to LLM API (sync call, blocks until acknowledged)
        String callbackUrl = callbackBaseUrl + "/api/callback/property";
        llmService.sendForExtraction(pdfBytes, fileName, session.getId(), callbackUrl);

        log.info("Extract request sent for session={}, file={}", session.getId(), fileName);

        return ExtractResponse.builder()
            .sessionId(session.getId())
            .status("processing")
            .build();
    }

    private ChatSession createNewSession(String fileName, Instant now, String userId) {
        return ChatSession.builder()
            .title(fileName != null ? fileName.replaceAll("\\.pdf$", "") : "New Analysis")
            .userId(userId)
            .messages(new ArrayList<>())
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
}
