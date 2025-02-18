package com.sparta.spring_deep._delivery.domain.ai;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleAiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.url}")
    private String aiUrl;

    @Value("${ai.key}")
    private String aiKey;

    public String createAiDescription(String menuDescription) {
        String requestUrl = aiUrl + aiKey;

        Map<String, Object> requestBody = Map.of(
            "contests", List.of(
                Map.of("parts", List.of(
                    Map.of("text", menuDescription + "이 메뉴설명에 대해서 50자 이하로 풀어서 답변해줘")
                ))
            )
        );

        Map<String, Object> response = restTemplate.postForObject(requestUrl, requestBody,
            Map.class);

        if (response != null && response.containsKey("candidates")) {
            var candidates = (List<Map<String, Object>>) response.get("candidates");
            if (!candidates.isEmpty()) {
                var content = (Map<String, Object>) candidates.get(0).get("content");
                var parts = (List<Map<String, Object>>) content.get("parts");
                if (!parts.isEmpty()) {
                    return parts.get(0).get("text").toString(); // AI 생성된 설명 반환
                }
            }
        }

        return "AI 설명을 생성할 수 없습니다.";

    }

}
