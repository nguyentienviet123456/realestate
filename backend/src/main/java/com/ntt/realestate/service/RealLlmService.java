package com.ntt.realestate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.realestate.dto.LlmChatResponse;
import com.ntt.realestate.dto.LlmRequest;
import com.ntt.realestate.dto.PropertyCallbackRequest;
import com.ntt.realestate.model.PropertyField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.llm.mock", havingValue = "false")
public class RealLlmService implements LlmService {

    private final PropertyCallbackService propertyCallbackService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.llm.api-url}")
    private String llmApiUrl;

    @Override
    public void sendForExtraction(byte[] pdfContent, String fileName, String sessionId, String callbackUrl) {
        log.info("RealLLM: Sending extraction request to {} for session={}, file={}", llmApiUrl, sessionId, fileName);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("message", "添付ファイルから物件・建物情報を抽出してください");
        body.add("extracted_data", buildExtractedDataTemplate(callbackUrl, sessionId, fileName));
        body.add("file", new ByteArrayResource(pdfContent) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<LlmChatResponse> response = restTemplate.exchange(
            llmApiUrl, HttpMethod.POST, requestEntity, LlmChatResponse.class
        );

        LlmChatResponse chatResponse = response.getBody();
        if (chatResponse == null) {
            throw new RuntimeException("Empty response from LLM API for session=" + sessionId);
        }

        log.info("RealLLM: Received response for session={}, message length={}",
            sessionId, chatResponse.getMessage() != null ? chatResponse.getMessage().length() : 0);

        List<PropertyField> fields = parseExtractedData(chatResponse.getExtractedData());

        if (fields.isEmpty()) {
            log.warn("RealLLM: No fields extracted for session={}", sessionId);
        }

        propertyCallbackService.processCallback(sessionId, fileName, fields);

        log.info("RealLLM: Processed {} fields for session={}", fields.size(), sessionId);
    }

    private String buildExtractedDataTemplate(String callbackUrl, String sessionId, String fileName) {
        LlmRequest template = new LlmRequest(callbackUrl, sessionId, fileName, buildEmptyFields());
        try {
            return objectMapper.writeValueAsString(template);
        } catch (Exception e) {
            log.error("Failed to serialize extracted_data template", e);
            return "{}";
        }
    }

    private List<PropertyField> buildEmptyFields() {
        List<PropertyField> fields = new ArrayList<>();

        // 1. 用途・法令・成立前提
        String cat1 = "用途・法令・成立前提";
        fields.add(emptyField(cat1, "isPurposeCompatible", "用途対応可否"));
        fields.add(emptyField(cat1, "heavyRestaurantAllowed", "重飲食可否"));
        fields.add(emptyField(cat1, "businessTypeRestriction", "業態制限"));
        fields.add(emptyField(cat1, "isPermitAcquisitionPossible", "必要許認可取得可否"));
        fields.add(emptyField(cat1, "zoningType", "用途地域"));
        fields.add(emptyField(cat1, "isActualOperationFeasible", "実態運用可否"));
        fields.add(emptyField(cat1, "complianceFlexibility", "消防・保健所対応余地"));
        fields.add(emptyField(cat1, "isAdultEntertainmentLawApplicable", "風営法該当性"));
        fields.add(emptyField(cat1, "signageLandscapeRegulation", "看板条例・景観"));

        // 2. 面積・階層・構造
        String cat2 = "面積・階層・構造";
        fields.add(emptyField(cat2, "privateAreaMeasured", "専有面積（実測）"));
        fields.add(emptyField(cat2, "effectiveArea", "有効面積"));
        fields.add(emptyField(cat2, "floorLevel", "所在階"));
        fields.add(emptyField(cat2, "basementAvailable", "地下可否"));
        fields.add(emptyField(cat2, "buildingStructure", "建物構造"));
        fields.add(emptyField(cat2, "adjacentFloorsUsage", "上下階用途"));

        // 3. 出入口・形状・視認性・段差
        String cat3 = "出入口・形状・視認性・段差";
        fields.add(emptyField(cat3, "entranceType", "出入口形態"));
        fields.add(emptyField(cat3, "entranceLocation", "出入口位置"));
        fields.add(emptyField(cat3, "entranceCount", "出入口数"));
        fields.add(emptyField(cat3, "effectiveFrontage", "有効間口"));
        fields.add(emptyField(cat3, "floorShape", "形状"));
        fields.add(emptyField(cat3, "isBarrierFree", "段差なし"));

        // 4. インフラ・設備
        String cat4 = "インフラ・設備";
        fields.add(emptyField(cat4, "ceilingHeightUnderBeam", "天井高（梁下）"));
        fields.add(emptyField(cat4, "ceilingVoidSpace", "天井内スペース"));
        fields.add(emptyField(cat4, "underfloorSpace", "床下スペース"));
        fields.add(emptyField(cat4, "floorLoadCapacity", "床荷重"));
        fields.add(emptyField(cat4, "exhaustSystemType", "排気方式"));
        fields.add(emptyField(cat4, "ductRoute", "ダクト経路"));
        fields.add(emptyField(cat4, "noiseVibrationConstraint", "防音・振動制約"));
        fields.add(emptyField(cat4, "powerCapacity", "電力容量"));
        fields.add(emptyField(cat4, "powerSupplySystem", "受電方式"));
        fields.add(emptyField(cat4, "isPowerExpansionPossible", "電力増設"));
        fields.add(emptyField(cat4, "gasCapacity", "ガス容量"));
        fields.add(emptyField(cat4, "plumbingPipeDiameter", "給排水管径"));
        fields.add(emptyField(cat4, "greaseTrap", "グリストラップ"));
        fields.add(emptyField(cat4, "hvacSystem", "空調方式"));
        fields.add(emptyField(cat4, "refrigerationEquipment", "冷蔵冷凍設備"));
        fields.add(emptyField(cat4, "interiorDesignFlexibility", "内装自由度"));

        // 5. 動線・運営条件
        String cat5 = "動線・運営条件";
        fields.add(emptyField(cat5, "customerFlowTraffic", "来店動線特性"));
        fields.add(emptyField(cat5, "frontRoadType", "前面道路種別"));
        fields.add(emptyField(cat5, "roadWidth", "道路幅員"));
        fields.add(emptyField(cat5, "intersectionProximity", "交差点位置"));
        fields.add(emptyField(cat5, "parkingLot", "駐車場"));
        fields.add(emptyField(cat5, "bicycleParking", "駐輪場"));
        fields.add(emptyField(cat5, "rainyDayAccess", "雨天時動線"));
        fields.add(emptyField(cat5, "loadingDock", "搬入口"));
        fields.add(emptyField(cat5, "deliveryTimeRestriction", "搬入時間制限"));
        fields.add(emptyField(cat5, "garbageArea", "ゴミ置場"));
        fields.add(emptyField(cat5, "garbageCollection", "ゴミ回収"));
        fields.add(emptyField(cat5, "hasElevator", "EV有無"));
        fields.add(emptyField(cat5, "elevatorUsageRestriction", "EV使用制限"));

        // 6. 契約・オーナー条件
        String cat6 = "契約・オーナー条件";
        fields.add(emptyField(cat6, "contractType", "契約形態"));
        fields.add(emptyField(cat6, "terminationClause", "中途解約条件"));
        fields.add(emptyField(cat6, "restorationCondition", "原状回復"));
        fields.add(emptyField(cat6, "renewalFee", "更新料"));
        fields.add(emptyField(cat6, "securityDeposit", "保証金"));
        fields.add(emptyField(cat6, "refundTerms", "返還条件"));
        fields.add(emptyField(cat6, "ownerScreeningCriteria", "オーナー審査基準"));
        fields.add(emptyField(cat6, "exclusivityClause", "同一業態排除"));
        fields.add(emptyField(cat6, "managementCompanyFlexibility", "管理会社柔軟性"));
        fields.add(emptyField(cat6, "asIsHandoverCondition", "現況渡し条件"));
        fields.add(emptyField(cat6, "runningCost", "ランニングコスト"));

        // 7. スケジュール・外部環境
        String cat7 = "スケジュール・外部環境";
        fields.add(emptyField(cat7, "handoverDate", "引渡可能日"));
        fields.add(emptyField(cat7, "fitOutPeriod", "内装工期余地"));
        fields.add(emptyField(cat7, "redevelopmentPlan", "再開発計画"));
        fields.add(emptyField(cat7, "competitorOpeningPlan", "競合出店予定"));
        fields.add(emptyField(cat7, "trafficChangeFactors", "人流変化要因"));
        fields.add(emptyField(cat7, "areaBranding", "エリアブランド"));

        // 8. 判断補助・失注防止
        String cat8 = "判断補助・失注防止";
        fields.add(emptyField(cat8, "criticalDealBreakers", "絶対NG該当"));
        fields.add(emptyField(cat8, "pastComplaints", "過去クレーム"));
        fields.add(emptyField(cat8, "noiseOdorTolerance", "騒音・匂い耐性"));
        fields.add(emptyField(cat8, "otherApplicationsStatus", "他申込状況"));
        fields.add(emptyField(cat8, "requirementFulfillmentRate", "条件充足率"));
        fields.add(emptyField(cat8, "priorityAlignment", "優先順位適合"));

        // 9. 立地・交通
        String cat9 = "立地・交通";
        fields.add(emptyField(cat9, "nearestStationName", "最寄駅名"));
        fields.add(emptyField(cat9, "walkTimeMinutes", "駅徒歩分数"));
        fields.add(emptyField(cat9, "availableLineCount", "利用路線数"));
        fields.add(emptyField(cat9, "nearestExit", "最寄出口"));
        fields.add(emptyField(cat9, "isStationConnected", "駅直結可否"));
        fields.add(emptyField(cat9, "hasBusStop", "バス停有無"));
        fields.add(emptyField(cat9, "nearestInterchangeName", "最寄IC名"));
        fields.add(emptyField(cat9, "interchangeDistance", "IC距離"));
        fields.add(emptyField(cat9, "largeVehicleAccess", "大型車進入可否"));
        fields.add(emptyField(cat9, "mainRoadAccess", "幹線道路接続"));
        fields.add(emptyField(cat9, "turningRestrictions", "右左折制限"));

        return fields;
    }

    private PropertyField emptyField(String category, String fieldName, String displayName) {
        return PropertyField.builder()
            .category(category)
            .fieldName(fieldName)
            .displayName(displayName)
            .value("")
            .status("pending")
            .build();
    }

    private List<PropertyField> parseExtractedData(String extractedData) {
        if (extractedData == null || extractedData.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(extractedData, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to parse extracted_data from LLM response: {}", e.getMessage());
            return List.of();
        }
    }
}
