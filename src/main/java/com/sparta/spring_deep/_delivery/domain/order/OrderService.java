package com.sparta.spring_deep._delivery.domain.order;

import com.sparta.spring_deep._delivery.domain.address.Address;
import com.sparta.spring_deep._delivery.domain.address.AddressRepository;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItem;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItemRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;


    // 주문 생성
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        User customer = userRepository.findById(requestDto.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("로그인 해주세요."));
        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
            .orElseThrow(() -> new IllegalArgumentException("음식점을 찾을 수 없습니다."));
        Address address = addressRepository.findById(requestDto.getAddressId())
            .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        Order order = orderRepository.save(new Order(customer, restaurant, address,
            requestDto.getTotalPrice(), requestDto.getRequest()));

//        Order order = orderRepository.save(new Order(
//            requestDto.getCustomerId(), requestDto.getRestaurantId(),
//            requestDto.getAddressId(), requestDto.getTotalPrice(), requestDto.getRequest()));
        return new OrderResponseDto(order);
    }


    // 주문 상태 변경
    @Transactional
    public OrderResponseDto updateOrderStatus(UUID orderId, OrderStatusEnum status, User customer) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityExistsException("order not found"));

        if (order.getStatus() == OrderStatusEnum.CANCELLED) {
            throw new IllegalStateException("Canceled orders cannot be updated");
        }

        order.updateOrderStatus(customer, status);

        return new OrderResponseDto(order);
    }


    // 주문 상세 조회
    public OrderDetailResponseDto getOrderDetail(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityExistsException("order not found"));

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(order.getId());

        return new OrderDetailResponseDto(order, orderItemList);
    }
}
