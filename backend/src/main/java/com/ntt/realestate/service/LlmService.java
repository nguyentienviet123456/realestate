package com.ntt.realestate.service;

import com.ntt.realestate.model.PropertyField;

import java.util.List;

public interface LlmService {
    List<PropertyField> analyzePdf(byte[] pdfContent, String fileName);
    String generateSummary(List<PropertyField> fields);
}
