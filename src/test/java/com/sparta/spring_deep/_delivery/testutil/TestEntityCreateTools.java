package com.sparta.spring_deep._delivery.testutil;

import static com.sparta.spring_deep._delivery.util.RestaurantAddressTools.searchAddress;
import static com.sparta.spring_deep._delivery.util.RestaurantAddressTools.validateTotalCount;

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
import java.util.Map;

public class TestEntityCreateTools {

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

}
