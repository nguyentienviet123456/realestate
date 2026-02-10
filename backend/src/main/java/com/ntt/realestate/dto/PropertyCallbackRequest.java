package com.ntt.realestate.dto;

import com.ntt.realestate.model.PropertyField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyCallbackRequest {
    private String sessionId;
    private String originalFileName;
    private List<PropertyField> fields;
}
