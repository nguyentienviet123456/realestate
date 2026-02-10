package com.ntt.realestate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String role;       // "user" or "assistant"
    private String content;
    private String type;       // "text", "pdf_upload", "analysis_result"
    private Instant timestamp;
}
