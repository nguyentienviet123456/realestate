package com.ntt.realestate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmChatResponse {
    private String message;

    @JsonProperty("extracted_data")
    private String extractedData;
}
