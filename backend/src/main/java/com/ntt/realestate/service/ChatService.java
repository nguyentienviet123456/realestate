package com.ntt.realestate.service;

import com.ntt.realestate.model.ChatMessage;
import com.ntt.realestate.model.ChatSession;
import com.ntt.realestate.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;

    public ChatSession sendMessage(String sessionId, String userId, String content) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (session.getMessages() == null) {
            session.setMessages(new ArrayList<>());
        }

        Instant now = Instant.now();

        // Add user message
        session.getMessages().add(ChatMessage.builder()
            .role("user").type("text").content(content).timestamp(now).build());

        // Generate mock AI response
        String aiResponse = generateMockResponse(content);
        session.getMessages().add(ChatMessage.builder()
            .role("assistant").type("text").content(aiResponse).timestamp(Instant.now()).build());

        session.setUpdatedAt(Instant.now());
        return chatSessionRepository.save(session);
    }

    private String generateMockResponse(String userMessage) {
        if (userMessage.contains("面積") || userMessage.contains("広さ")) {
            return "この物件の専有面積は150.00㎡（実測）、有効面積は135.00㎡です。\n1階に位置し、地下1階も利用可能です。";
        }
        if (userMessage.contains("賃料") || userMessage.contains("費用") || userMessage.contains("コスト")) {
            return "賃料の詳細は以下の通りです：\n・保証金：賃料10ヶ月分\n・更新料：新賃料の1ヶ月分\n・共益費：¥55,000/月 + 水道基本料\n・返還条件：解約時全額返還（償却なし）";
        }
        if (userMessage.contains("駅") || userMessage.contains("アクセス") || userMessage.contains("交通")) {
            return "最寄駅は六本木駅（東京メトロ日比谷線）で徒歩3分です。\n大江戸線も利用可能で、2路線が使えます。\nバス停も徒歩1分の距離にあります。";
        }
        if (userMessage.contains("設備") || userMessage.contains("インフラ")) {
            return "主要設備情報：\n・天井高：3,200mm（梁下）\n・電力：60A（3相200V）、増設可（最大100Aまで）\n・ガス：都市ガス13A・10号\n・空調：個別空調（天カセ4方向）\n・排気：個別排気可（屋上ダクト）";
        }
        if (userMessage.contains("契約") || userMessage.contains("条件")) {
            return "契約条件の概要：\n・契約形態：普通借家契約（2年更新）\n・中途解約：6ヶ月前予告\n・原状回復：スケルトン戻し\n・引渡し：即日可能（現在空室）\n・内装工事期間：約2〜3ヶ月の余地あり";
        }
        return "ご質問ありがとうございます。\n\n「" + userMessage + "」について確認いたします。\n" +
            "この物件は六本木エリアに位置する商業物件で、飲食店舗としての利用に適しています。\n" +
            "詳細については、右側の物件情報パネルもご参照ください。";
    }
}
