package com.ntt.realestate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "property_details")
public class PropertyDetails {
    @Id
    private String id;
    private String sessionId;
    private String originalFileName;
    private List<PropertyField> fields;
    private Instant createdAt;
    private Instant updatedAt;
}
