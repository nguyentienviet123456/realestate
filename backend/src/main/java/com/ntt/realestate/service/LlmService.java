package com.ntt.realestate.service;

import com.ntt.realestate.model.ChatMessage;

import java.util.List;

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
     * @param chatHistory  previous messages in the conversation
     */
    void sendForExtraction(byte[] pdfContent, String fileName, String sessionId, String callbackUrl, List<ChatMessage> chatHistory);

    /**
     * Send a chat message to the LLM API and return the response.
     *
     * @param message      the user's message
     * @param chatHistory  previous messages in the conversation
     * @return the LLM's response text
     */
    String sendChatMessage(String message, List<ChatMessage> chatHistory);
}
