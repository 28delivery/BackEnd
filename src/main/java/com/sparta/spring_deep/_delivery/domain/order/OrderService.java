package com.sparta.spring_deep._delivery.domain.order;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        return null;
    }

    public OrderResponseDto updateOrder(OrderRequestDto requestDto) {
        return null;
    }


    public OrderResponseDto getOrderDetails(UUID orderId) {
        return null;
    }

    public Pageable getMyOrderList() {
        return null;
    }
}
