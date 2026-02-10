package com.ntt.realestate.service;

import com.ntt.realestate.dto.AnalyzeResponse;
import com.ntt.realestate.model.ChatMessage;
import com.ntt.realestate.model.ChatSession;
import com.ntt.realestate.model.PropertyDetails;
import com.ntt.realestate.model.PropertyField;
import com.ntt.realestate.repository.ChatSessionRepository;
import com.ntt.realestate.repository.PropertyDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final LlmService llmService;
    private final ChatSessionRepository chatSessionRepository;
    private final PropertyDetailsRepository propertyDetailsRepository;

    public AnalyzeResponse analyzePdf(MultipartFile file, String sessionId, String userId) throws IOException {
        byte[] pdfBytes = file.getBytes();
        String fileName = file.getOriginalFilename();

        List<PropertyField> fields = llmService.analyzePdf(pdfBytes, fileName);
        String summary = llmService.generateSummary(fields);

        Instant now = Instant.now();

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
        session.getMessages().add(ChatMessage.builder()
            .role("assistant").type("analysis_result")
            .content(summary)
            .timestamp(now).build());
        session.setUpdatedAt(now);

        session = chatSessionRepository.save(session);

        PropertyDetails details = PropertyDetails.builder()
            .sessionId(session.getId())
            .originalFileName(fileName)
            .fields(fields)
            .createdAt(now)
            .updatedAt(now)
            .build();
        details = propertyDetailsRepository.save(details);

        session.setPropertyDetailsId(details.getId());
        chatSessionRepository.save(session);

        return AnalyzeResponse.builder()
            .sessionId(session.getId())
            .propertyDetailsId(details.getId())
            .fields(fields)
            .summary(summary)
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
