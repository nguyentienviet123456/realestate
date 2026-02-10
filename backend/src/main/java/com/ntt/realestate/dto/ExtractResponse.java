package com.ntt.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtractResponse {
    private String sessionId;
    private String status; // "processing", "completed", "failed"
}
