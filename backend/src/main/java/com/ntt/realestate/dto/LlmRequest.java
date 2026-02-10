package com.ntt.realestate.dto;

import com.ntt.realestate.model.PropertyField;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LlmRequest {
    private String callback;
    private String sessionId;
    private String originalFileName;
    private List<PropertyField> fields;
}
