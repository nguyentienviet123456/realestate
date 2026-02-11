package com.ntt.realestate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmChatResponse {
    private String response;

    @JsonProperty("conversation_id")
    private String conversationId;

    private String model;

    private String timestamp;

    @JsonProperty("tokens_used")
    private Integer tokensUsed;

    @JsonProperty("save_database")
    private PropertyCallbackRequest saveDatabase;
}
