package com.ntt.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionSummary {
    private String id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
}
