package com.ntt.realestate.service;

public interface LlmService {
    /**
     * Send PDF to LLM API for property extraction.
     * This call is synchronous — blocks until LLM API acknowledges receipt.
     * LLM processes async and calls callbackUrl with results when done.
     *
     * @param pdfContent   raw PDF bytes
     * @param fileName     original file name
     * @param sessionId    session to associate results with
     * @param callbackUrl  URL the LLM should POST results to
     */
    void sendForExtraction(byte[] pdfContent, String fileName, String sessionId, String callbackUrl);
}
