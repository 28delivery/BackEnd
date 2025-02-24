package com.sparta.spring_deep._delivery.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RestaurantAddressTools {

    private static final RestTemplate restTemplate = new RestTemplate();

    @Value("${restaurantAddress.url}")
    private static String restaurantAddressUrl;

    @Value("${restaurantAddress.key}")
    private static String restaurantAddressKey;

    /**
     * keyword로 주소 검색 후 Json(Map) 형태로 검색 결과를 반환합니다.
     *
     * @param keyword 주소 검색을 위한 검색어
     */
    public static Map<String, Object> searchAddress(String keyword) {
        try {
            // JSON 형식의 결과를 요청하기 위한 설정
            String resultType = "json";

            // URI 빌드 (내부적으로 인코딩된 URL 생성)
            String encodedUrl = UriComponentsBuilder.fromHttpUrl(restaurantAddressUrl)
                .queryParam("confmKey", restaurantAddressKey)
                .queryParam("currentPage", 1)
                .queryParam("countPerPage", 10)
                .queryParam("keyword", keyword)
                .queryParam("resultType", resultType)
                .toUriString();

            // 디코딩된 URL로 변환 (API 서버가 요구하는 형식)
            String decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
            System.out.println("Decoded URL: " + decodedUrl);

            // API 호출
            ResponseEntity<String> response = restTemplate.getForEntity(decodedUrl, String.class);
            String jsonResponse = response.getBody();

            // JSON 문자열을 Map으로 파싱
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new InternalException("API 호출 중 오류 발생: " + ex.getMessage(), ex);
        }
    }

    /**
     * API 응답 결과에서 totalCount가 1인지 검사합니다. totalCount가 1이 아니면 IllegalStateException을 발생시킵니다.
     *
     * @param responseMap API 응답을 파싱한 Map 객체
     */
    public static Map<String, Object> validateTotalCount(Map<String, Object> responseMap) {
        if (responseMap == null || !responseMap.containsKey("results")) {
            throw new IllegalArgumentException("유효한 응답 데이터가 아닙니다.");
        }

        // "results" 객체에서 "common" 추출
        Map<String, Object> results = (Map<String, Object>) responseMap.get("results");
        if (results == null || !results.containsKey("common")) {
            throw new IllegalArgumentException("응답에 'common' 객체가 없습니다.");
        }

        Map<String, Object> common = (Map<String, Object>) results.get("common");
        Object totalCountObj = common.get("totalCount");
        if (totalCountObj == null) {
            throw new IllegalArgumentException("응답에 totalCount 값이 없습니다.");
        }

        // totalCount 값은 문자열로 전달되므로 정수형으로 변환
        try {
            int totalCount = Integer.parseInt(totalCountObj.toString());
            if (totalCount != 1) {
                throw new IllegalStateException(
                    "totalCount가 1이 아닙니다. 현재 totalCount: " + totalCount);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("totalCount가 정수형 값이 아닙니다: " + totalCountObj, e);
        }

        return ((List<Map<String, Object>>) results.get("juso")).get(0);
    }

}
