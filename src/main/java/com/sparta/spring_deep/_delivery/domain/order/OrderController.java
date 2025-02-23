package com.sparta.spring_deep._delivery.domain.order;

import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsRequestDto;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsResponseDto;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping("/orders")
    public ResponseEntity<OrderDetailsResponseDto> createOrder(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody OrderDetailsRequestDto requestDto
    ) {
        log.info("Create Order : {}", requestDto);

        OrderDetailsResponseDto responseDto = orderService.createOrder(requestDto,
            userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 주문 상태 변경 - OWNER & Manager
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
        @AuthenticationPrincipal UserDetailsImpl owner,
        @PathVariable UUID orderId,
        @RequestParam OrderStatusEnum status
    ) {
        log.info("Update Order Status : {}", orderId);

        OrderResponseDto orderResponseDto = orderService.updateOrderStatus(orderId, status,
            owner.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);
    }

    // 주문 상세 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailsResponseDto> getOrderDetails(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID orderId) {
        log.info("주문 상세 조회 요청 - orderId : {}", orderId);

        OrderDetailsResponseDto responseDto = orderService.getOrderDetails(userDetails.getUser(),
            orderId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 나의 주문 내역 조회
    @GetMapping("/orders/me")
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PageableDefault(size = 10, page = 0, direction = Direction.DESC, sort = "createdAt") Pageable pageable,
        @RequestParam(required = false, defaultValue = "null") String menu,
        @RequestParam(required = false, defaultValue = "null") String restaurant
    ) {
        log.info("나의 주문 내역 조회 요청 ");

        Page<OrderResponseDto> responseDtos = orderService.getMyOrders(userDetails.getUser(),
            pageable, menu, restaurant);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    // 주문 취소 (주문 5분 이내)
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<String> canceledOrder(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID orderId
    ) {
        log.info("주문 취소 요청 - orderId : {}", orderId);

        return orderService.canceledOrder(userDetails.getUser(), orderId);
    }

    // 실시간 주문 확인 (프론트 주기적 호출)
    @GetMapping("/orders/polling")
    public ResponseEntity<Page<OrderResponseDto>> pollingOrder(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {

        log.info("실시간 주문 확인 - customerId : {}", userDetails.getUsername());
        Page<OrderResponseDto> updatedOrdersSince = orderService.getUpdatedOrdersSince(
            userDetails.getUser(), pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.status(HttpStatus.OK).body(updatedOrdersSince);
    }

    // 주문 내역 삭제
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<String> deletedOrder(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID orderId
    ) {
        log.info("주문 내역 삭제 요청 - orderId : {}", orderId);

        return orderService.deletedOrder(userDetails.getUser(), orderId);
    }


}
