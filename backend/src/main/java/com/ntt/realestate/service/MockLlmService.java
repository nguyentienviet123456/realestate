package com.ntt.realestate.service;

import com.ntt.realestate.model.PropertyField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MockLlmService implements LlmService {

    @Override
    public List<PropertyField> analyzePdf(byte[] pdfContent, String fileName) {
        List<PropertyField> fields = new ArrayList<>();

        // 土地の情報 (Land Information)
        fields.add(PropertyField.builder().category("土地の情報").fieldName("所在地").value("東京都港区六本木1-1-1").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("土地面積").value("500.00㎡").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("権利").value("所有権").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("地目").value("宅地").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("現況").value("更地").status("pending").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("都市計画").value("市街化区域").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("用途地域").value("商業地域").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("建蔽率").value("80%").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("容積率").value("600%").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("私道有無").value("無").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("接面道路").value("南側6m公道").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("容積率制限").value("なし").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("地価・路線価").value("1,200,000円/㎡").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("用途駅").value("六本木駅 徒歩3分").status("done").build());
        fields.add(PropertyField.builder().category("土地の情報").fieldName("最寄IC").value("首都高 飯倉IC").status("pending").build());

        // 建物の情報 (Building Information)
        fields.add(PropertyField.builder().category("建物の情報").fieldName("延床面積").value("2,800.00㎡").status("done").build());
        fields.add(PropertyField.builder().category("建物の情報").fieldName("専有面積").value("2,500.00㎡").status("done").build());
        fields.add(PropertyField.builder().category("建物の情報").fieldName("基準階面積").value("280.00㎡").status("done").build());
        fields.add(PropertyField.builder().category("建物の情報").fieldName("建物階数").value("地上10階・地下1階").status("done").build());
        fields.add(PropertyField.builder().category("建物の情報").fieldName("所在階数").value("1〜10階").status("done").build());
        fields.add(PropertyField.builder().category("建物の情報").fieldName("駐車場").value("機械式20台").status("pending").build());
        fields.add(PropertyField.builder().category("建物の情報").fieldName("現況用途").value("事務所").status("done").build());

        // 希望条件 (Desired Conditions)
        fields.add(PropertyField.builder().category("希望条件").fieldName("期限").value("2024年6月末").status("pending").build());
        fields.add(PropertyField.builder().category("希望条件").fieldName("希望価格").value("15億円").status("done").build());
        fields.add(PropertyField.builder().category("希望条件").fieldName("賃料相場").value("15,000円/坪").status("done").build());
        fields.add(PropertyField.builder().category("希望条件").fieldName("賃貸想定価格").value("月額3,750万円").status("done").build());

        // NOI/利回り (NOI/Yield)
        fields.add(PropertyField.builder().category("NOI/利回り").fieldName("NOI").value("96,000,000円").status("done").build());
        fields.add(PropertyField.builder().category("NOI/利回り").fieldName("利回り").value("6.4%").status("done").build());

        // 特記事項 (Special Notes)
        fields.add(PropertyField.builder().category("特記事項").fieldName("備考").value("アスベスト調査済み（問題なし）、耐震補強工事実施済み（2018年）").status("done").build());

        return fields;
    }

    @Override
    public String generateSummary(List<PropertyField> fields) {
        long doneCount = fields.stream().filter(f -> "done".equals(f.getStatus())).count();
        long pendingCount = fields.stream().filter(f -> "pending".equals(f.getStatus())).count();

        return String.format(
            "（物件情報を含むファイルを読み取り、周辺情報の取得、整理して出力）\n" +
            "読み取りが完了しました。以下に結果を示します。不足や訂正があればご指示ください。\n\n" +
            "抽出結果：%d件完了、%d件確認中",
            doneCount, pendingCount
        );
    }
}
