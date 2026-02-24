package com.ntt.realestate.service;

import com.ntt.realestate.model.ChatMessage;
import com.ntt.realestate.model.PropertyField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock LLM implementation for development.
 * Simulates the real LLM flow: generates mock fields and saves via PropertyCallbackService
 * (as if the real LLM had called the callback endpoint).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.llm.mock", havingValue = "true", matchIfMissing = true)
public class MockLlmService implements LlmService {

    private final PropertyCallbackService propertyCallbackService;

    @Override
    public void sendForExtraction(byte[] pdfContent, String fileName, String sessionId, String callbackUrl, List<ChatMessage> chatHistory) {
        log.info("MockLLM: Simulating extraction for session={}, file={}", sessionId, fileName);

        // Generate mock fields (simulating LLM extraction)
        List<PropertyField> fields = generateMockFields();

        // Simulate LLM calling the callback (saves PropertyDetails + updates session)
        propertyCallbackService.processCallback(sessionId, fileName, fields);

        log.info("MockLLM: Callback simulated — {} fields saved for session={}", fields.size(), sessionId);
    }

    @Override
    public String sendChatMessage(String message, List<ChatMessage> chatHistory) {
        log.info("MockLLM: Generating mock chat response for message: {}", message);
        return generateMockChatResponse(message);
    }

    private String generateMockChatResponse(String userMessage) {
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

    private List<PropertyField> generateMockFields() {
        List<PropertyField> fields = new ArrayList<>();

        // 1. 用途・法令・成立前提
        String cat1 = "用途・法令・成立前提";
        fields.add(field(cat1, "isPurposeCompatible", "用途対応可否", "対応可", "done"));
        fields.add(field(cat1, "heavyRestaurantAllowed", "重飲食可否", "可（条件付き）", "done"));
        fields.add(field(cat1, "businessTypeRestriction", "業態制限", "風俗営業不可", "done"));
        fields.add(field(cat1, "isPermitAcquisitionPossible", "必要許認可取得可否", "取得可能", "done"));
        fields.add(field(cat1, "zoningType", "用途地域", "商業地域", "done"));
        fields.add(field(cat1, "isActualOperationFeasible", "実態運用可否", "可", "done"));
        fields.add(field(cat1, "complianceFlexibility", "消防・保健所対応余地", "対応余地あり", "pending"));
        fields.add(field(cat1, "isAdultEntertainmentLawApplicable", "風営法該当性", "非該当", "done"));
        fields.add(field(cat1, "signageLandscapeRegulation", "看板条例・景観", "景観条例エリア外", "pending"));

        // 2. 面積・階層・構造
        String cat2 = "面積・階層・構造";
        fields.add(field(cat2, "privateAreaMeasured", "専有面積（実測）", "150.00㎡", "done"));
        fields.add(field(cat2, "effectiveArea", "有効面積", "135.00㎡", "done"));
        fields.add(field(cat2, "floorLevel", "所在階", "1階", "done"));
        fields.add(field(cat2, "basementAvailable", "地下可否", "地下1階あり", "done"));
        fields.add(field(cat2, "buildingStructure", "建物構造", "SRC造（鉄骨鉄筋コンクリート）", "done"));
        fields.add(field(cat2, "adjacentFloorsUsage", "上下階用途", "2F: 事務所 / B1: 倉庫", "pending"));

        // 3. 出入口・形状・視認性・段差
        String cat3 = "出入口・形状・視認性・段差";
        fields.add(field(cat3, "entranceType", "出入口形態", "路面店舗・独立入口", "done"));
        fields.add(field(cat3, "entranceLocation", "出入口位置", "南側（大通り面）", "done"));
        fields.add(field(cat3, "entranceCount", "出入口数", "2箇所", "done"));
        fields.add(field(cat3, "effectiveFrontage", "有効間口", "8.5m", "done"));
        fields.add(field(cat3, "floorShape", "形状", "整形（長方形）", "done"));
        fields.add(field(cat3, "isBarrierFree", "段差なし", "あり（スロープ設置済）", "done"));

        // 4. インフラ・設備
        String cat4 = "インフラ・設備";
        fields.add(field(cat4, "ceilingHeightUnderBeam", "天井高（梁下）", "3,200mm", "done"));
        fields.add(field(cat4, "ceilingVoidSpace", "天井内スペース", "約600mm", "done"));
        fields.add(field(cat4, "underfloorSpace", "床下スペース", "約300mm", "pending"));
        fields.add(field(cat4, "floorLoadCapacity", "床荷重", "500kg/㎡", "done"));
        fields.add(field(cat4, "exhaustSystemType", "排気方式", "個別排気可（屋上ダクト）", "done"));
        fields.add(field(cat4, "ductRoute", "ダクト経路", "PS経由→屋上排気", "done"));
        fields.add(field(cat4, "noiseVibrationConstraint", "防音・振動制約", "22時以降制限あり", "done"));
        fields.add(field(cat4, "powerCapacity", "電力容量", "60A（3相200V）", "done"));
        fields.add(field(cat4, "powerSupplySystem", "受電方式", "高圧一括受電", "done"));
        fields.add(field(cat4, "isPowerExpansionPossible", "電力増設", "増設可（最大100Aまで）", "pending"));
        fields.add(field(cat4, "gasCapacity", "ガス容量", "都市ガス13A・10号", "done"));
        fields.add(field(cat4, "plumbingPipeDiameter", "給排水管径", "給水25mm / 排水75mm", "done"));
        fields.add(field(cat4, "greaseTrap", "グリストラップ", "設置済（200L）", "done"));
        fields.add(field(cat4, "hvacSystem", "空調方式", "個別空調（天カセ4方向）", "done"));
        fields.add(field(cat4, "refrigerationEquipment", "冷蔵冷凍設備", "設置スペースあり", "pending"));
        fields.add(field(cat4, "interiorDesignFlexibility", "内装自由度", "スケルトン渡し・自由", "done"));

        // 5. 動線・運営条件
        String cat5 = "動線・運営条件";
        fields.add(field(cat5, "customerFlowTraffic", "来店動線特性", "駅徒歩3分・商店街通り沿い", "done"));
        fields.add(field(cat5, "frontRoadType", "前面道路種別", "区道（歩道付き）", "done"));
        fields.add(field(cat5, "roadWidth", "道路幅員", "12m（歩道3m含む）", "done"));
        fields.add(field(cat5, "intersectionProximity", "交差点位置", "交差点角地", "done"));
        fields.add(field(cat5, "parkingLot", "駐車場", "近隣コインパーキング（50m以内）", "done"));
        fields.add(field(cat5, "bicycleParking", "駐輪場", "ビル共用駐輪場あり（10台）", "done"));
        fields.add(field(cat5, "rainyDayAccess", "雨天時動線", "アーケードなし・屋根付き入口", "pending"));
        fields.add(field(cat5, "loadingDock", "搬入口", "裏口搬入口あり", "done"));
        fields.add(field(cat5, "deliveryTimeRestriction", "搬入時間制限", "7:00〜9:00 / 搬入車両制限あり", "done"));
        fields.add(field(cat5, "garbageArea", "ゴミ置場", "ビル共用ゴミ置場（1F裏）", "done"));
        fields.add(field(cat5, "garbageCollection", "ゴミ回収", "週3回（月水金）", "done"));
        fields.add(field(cat5, "hasElevator", "EV有無", "あり（1基）", "done"));
        fields.add(field(cat5, "elevatorUsageRestriction", "EV使用制限", "搬入時間帯のみ貨物利用可", "pending"));

        // 6. 契約・オーナー条件
        String cat6 = "契約・オーナー条件";
        fields.add(field(cat6, "contractType", "契約形態", "普通借家契約（2年更新）", "done"));
        fields.add(field(cat6, "terminationClause", "中途解約条件", "6ヶ月前予告", "done"));
        fields.add(field(cat6, "restorationCondition", "原状回復", "スケルトン戻し", "done"));
        fields.add(field(cat6, "renewalFee", "更新料", "新賃料の1ヶ月分", "done"));
        fields.add(field(cat6, "securityDeposit", "保証金", "賃料10ヶ月分", "done"));
        fields.add(field(cat6, "refundTerms", "返還条件", "解約時全額返還（償却なし）", "done"));
        fields.add(field(cat6, "ownerScreeningCriteria", "オーナー審査基準", "法人契約・業態審査あり", "pending"));
        fields.add(field(cat6, "exclusivityClause", "同一業態排除", "同一ビル内に飲食店なし", "done"));
        fields.add(field(cat6, "managementCompanyFlexibility", "管理会社柔軟性", "比較的柔軟", "done"));
        fields.add(field(cat6, "asIsHandoverCondition", "現況渡し条件", "スケルトン", "done"));
        fields.add(field(cat6, "runningCost", "ランニングコスト", "共益費 ¥55,000/月 + 水道基本料", "done"));
        fields.add(field(cat6, "price", "賃料/価格", "月額 ¥850,000（税別）", "done"));

        // 7. スケジュール・外部環境
        String cat7 = "スケジュール・外部環境";
        fields.add(field(cat7, "handoverDate", "引渡可能日", "即日（現在空室）", "done"));
        fields.add(field(cat7, "fitOutPeriod", "内装工期余地", "約2〜3ヶ月", "done"));
        fields.add(field(cat7, "redevelopmentPlan", "再開発計画", "周辺再開発予定なし", "done"));
        fields.add(field(cat7, "competitorOpeningPlan", "競合出店予定", "半径200m以内に同業出店情報なし", "pending"));
        fields.add(field(cat7, "trafficChangeFactors", "人流変化要因", "新駅出口開設予定（2025年）", "done"));
        fields.add(field(cat7, "areaBranding", "エリアブランド", "六本木・飲食激戦区", "done"));

        // 8. 判断補助・失注防止
        String cat8 = "判断補助・失注防止";
        fields.add(field(cat8, "criticalDealBreakers", "絶対NG該当", "該当なし", "done"));
        fields.add(field(cat8, "pastComplaints", "過去クレーム", "騒音クレーム1件（2022年・解決済）", "done"));
        fields.add(field(cat8, "noiseOdorTolerance", "騒音・匂い耐性", "中程度（住居混在エリア）", "done"));
        fields.add(field(cat8, "otherApplicationsStatus", "他申込状況", "現在なし", "pending"));

        // 9. 立地・交通
        String cat9 = "立地・交通";
        fields.add(field(cat9, "nearestStationName", "最寄駅名", "六本木駅（東京メトロ日比谷線）", "done"));
        fields.add(field(cat9, "walkTimeMinutes", "駅徒歩分数", "3分", "done"));
        fields.add(field(cat9, "availableLineCount", "利用路線数", "2路線（日比谷線・大江戸線）", "done"));
        fields.add(field(cat9, "nearestExit", "最寄出口", "3番出口", "done"));
        fields.add(field(cat9, "isStationConnected", "駅直結可否", "非直結", "done"));
        fields.add(field(cat9, "hasBusStop", "バス停有無", "あり（六本木停留所・徒歩1分）", "done"));
        fields.add(field(cat9, "nearestInterchangeName", "最寄IC名", "首都高 飯倉IC", "done"));
        fields.add(field(cat9, "interchangeDistance", "IC距離", "約800m", "done"));
        fields.add(field(cat9, "largeVehicleAccess", "大型車進入可否", "不可（4t車まで）", "done"));
        fields.add(field(cat9, "mainRoadAccess", "幹線道路接続", "外苑東通り直結", "done"));
        fields.add(field(cat9, "turningRestrictions", "右左折制限", "右折禁止（朝7-9時）", "pending"));
        fields.add(field(cat9, "address", "住所", "東京都港区六本木7-18-5", "done"));

        // 10. オーナー譲れない条件
        String cat10 = "オーナー譲れない条件";
        fields.add(field(cat10, "conditionOwnerMusthave", "オーナー譲れない条件", "飲食業態は要事前審査、保証金10ヶ月以上", "done"));

        // 11. オーナー重視・柔軟対応
        String cat11 = "オーナー重視・柔軟対応";
        fields.add(field(cat11, "conditionOwnerFlexible", "オーナー重視・柔軟対応", "内装工事期間中のフリーレント相談可", "pending"));

        // 12. オーナーNG条件
        String cat12 = "オーナーNG条件";
        fields.add(field(cat12, "conditionNg", "オーナーNG条件", "風俗営業・深夜営業（24時以降）不可", "done"));

        return fields;
    }

    private PropertyField field(String category, String fieldName, String displayName, String value, String status) {
        return PropertyField.builder()
            .category(category)
            .fieldName(fieldName)
            .displayName(displayName)
            .value(value)
            .status(status)
            .build();
    }
}
