package com.ntt.realestate.dto;

import com.ntt.realestate.model.PropertyField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzeResponse {
    private String sessionId;
    private String propertyDetailsId;
    private List<PropertyField> fields;
    private String summary;
}
