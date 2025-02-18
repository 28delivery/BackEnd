package com.sparta.spring_deep._delivery.domain.order;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Slf4j(topic = "Order Controller")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
//    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
        @RequestBody OrderRequestDto orderRequestDto) {

        log.info("Create Order: {}", orderRequestDto);
        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }


    // 주문 상태 변경
    //    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(
        @PathVariable UUID orderId,
        @RequestBody OrderRequestDto orderRequestDto) {

        log.info("Update Order: {}", orderRequestDto);
        OrderResponseDto orderResponseDto = orderService.updateOrder(orderRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(
        @PathVariable UUID orderId) {

        log.info("Update Order with Id: {}", orderId);
        OrderResponseDto orderResponseDto = orderService.getOrderDetails(orderId);

        return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);
    }

    // 사용자 정보 token으로 받기
    // 나의 주문 내역 조회
    @GetMapping("/me")
    public ResponseEntity<Pageable> updateOrder() {

        log.info("Update Order with Id: {}", "admin");
        Pageable orderResponseDtoPageable = orderService.getMyOrderList();

        return ResponseEntity.status(HttpStatus.OK).body(orderResponseDtoPageable);
    }

    // 주문 최소 (주문 5분 이내)

    // 실시간 주문 확인용 polling API


}
