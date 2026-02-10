package com.ntt.realestate.controller;

import com.ntt.realestate.dto.PropertyCallbackRequest;
import com.ntt.realestate.service.PropertyCallbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Callback endpoint for LLM API to deliver property extraction results.
 * This endpoint is unauthenticated (called by external LLM service).
 */
@RestController
@RequestMapping("/api/callback")
@RequiredArgsConstructor
public class PropertyCallbackController {

    private final PropertyCallbackService propertyCallbackService;

    @PostMapping("/property")
    public ResponseEntity<Void> receivePropertyData(@RequestBody PropertyCallbackRequest request) {
        propertyCallbackService.processCallback(
            request.getSessionId(),
            request.getOriginalFileName(),
            request.getFields()
        );
        return ResponseEntity.ok().build();
    }
}
