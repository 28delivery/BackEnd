package com.sparta.spring_deep._delivery.domain.order;

import com.sparta.spring_deep._delivery.domain.user.entity.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
        @RequestBody OrderRequestDto orderRequestDto
    ) {
        log.info("Create Order : {}", orderRequestDto);
        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    // 주문 상태 변경
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
        @PathVariable UUID orderId,
        @RequestParam OrderStatusEnum status
    ) {
        log.info("Update Order Status : {}", orderId);

        User customer = new User(); //임시
        OrderResponseDto orderResponseDto = orderService.updateOrderStatus(orderId, status,
            customer);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponseDto> getOrder(@PathVariable UUID orderId) {
        log.info("주문 조회 요청 - orderId : {}", orderId);

        OrderDetailResponseDto orderDetailResponseDto = orderService.getOrderDetail(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderDetailResponseDto);
    }

//    // 나의 주문 내역 조회
//    @GetMapping("/{userId}")
//    public ResponseEntity<OrderResponseDto> getOrderByUserId(@PathVariable UUID userId) {
//        log.info("나의 주문 내역 조회 - userId : {}", userId);
//        OrderResponseDto orderResponseDto = orderService.get
//
//    }

}
