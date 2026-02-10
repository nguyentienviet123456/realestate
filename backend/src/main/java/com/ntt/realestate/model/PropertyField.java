package com.ntt.realestate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyField {
    private String fieldName;
    private String displayName;
    private String value;
    private String status;   // "done" or "pending"
    private String category;
}
