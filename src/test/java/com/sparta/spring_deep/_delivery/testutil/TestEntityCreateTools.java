package com.sparta.spring_deep._delivery.testutil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.ai.Ai;
import com.sparta.spring_deep._delivery.domain.menu.Menu;
import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItem;
import com.sparta.spring_deep._delivery.domain.payment.Payment;
import com.sparta.spring_deep._delivery.domain.payment.Payment.PaymentStatusEnum;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
import com.sparta.spring_deep._delivery.domain.restaurant.restaurantAddress.RestaurantAddress;
import com.sparta.spring_deep._delivery.domain.review.Review;
import com.sparta.spring_deep._delivery.domain.user.entity.IsPublic;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class TestEntityCreateTools {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final String restaurantAddressUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do";
    private static final String restaurantAddressKey = "devU01TX0FVVEgyMDI1MDIxOTE4MzczNDExNTQ4ODQ=";

    // 유저 생성
    public static User createUser(String username, UserRole role) {
        return User.builder()
            .username(username)
            .password(username)
            .email(username + "@test.com")
            .role(role)
            .isPublic(IsPublic.PUBLIC)
            .build();
    }

    public static Address createAddress(User user, String address, String addressName) {
        return Address.builder()
            .user(user)
            .address(address)
            .addressName(addressName)
            .build();
    }

    // 가게 주소
    public static RestaurantAddress createRestaurantAddress(String username) {
        // 주소 검색
        Map<String, Object> searchResultJson = searchAddress("논현로 111길 21");

        // 주소 검색 결과 유효성 검사 후 Juso 부분 반환
        Map<String, Object> resultsJuso = validateTotalCount(searchResultJson);

        // 주소 검색 결과로 가게 주소 객체 생성
        return RestaurantAddress.builder()
            .resultsJuso(resultsJuso)
            .detailAddr("강남빌딩 202호")
            .username(username)
            .build();
    }

    // 가게
    public static Restaurant createRestaurant(User user, RestaurantAddress restaurantAddress,
        String name,
        String phone) {
        return Restaurant.builder()
            .owner(user)
            .name(name)
            .category(CategoryEnum.HANSIK)
            .restaurantAddress(restaurantAddress)
            .phone(phone)
            .build();
    }

    // 메뉴
    public static Menu createMenu(User user, Restaurant restaurant, String menuName,
        String description,
        Double price) {
        return Menu.builder()
            .restaurant(restaurant)
            .name(menuName)
            .description(description)
            .price(BigDecimal.valueOf(price))
            .isHidden(false)
            .user(user)
            .build();
    }

    // 메뉴 AI
    public static Ai createAi(Menu menu, String request, String response, User user) {
        return Ai.builder()
            .menu(menu)
            .request(request)
            .response(response)
            .user(user)
            .build();
    }

    public static Payment createPayment(
        String username,
        Order order,
        BigDecimal amount,
        PaymentStatusEnum paymentStatus
    ) {
        return Payment.builder()
            .username(username)
            .order(order)
            .amount(amount)
            .paymentStatus(paymentStatus)
            .build();

    }

    // 주문
    public static Order createOrder(User customer, Restaurant restaurant, Address address,
        Double totalPrice, String request) {
        return Order.builder()
            .customer(customer)
            .restaurant(restaurant)
            .address(address)
            .totalPrice(BigDecimal.valueOf(totalPrice))
            .request(request)
            .build();
    }

    // 주문 아이템
    public static OrderItem createOrderItem(Order order, Menu menu, int quantity,
        Double price) {
        return OrderItem.builder()
            .order(order)
            .menu(menu)
            .quantity(quantity)
            .price(BigDecimal.valueOf(price))
            .build();
    }

    //리뷰
    public static Review createReview(Order order, User user, int rating, String comment) {
        return Review.builder()
            .order(order)
            .user(user)
            .rating(rating)
            .comment(comment)
            .build();
    }

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
