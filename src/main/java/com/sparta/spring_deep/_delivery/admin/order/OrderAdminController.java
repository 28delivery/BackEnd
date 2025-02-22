package com.sparta.spring_deep._delivery.admin.order;

import com.sparta.spring_deep._delivery.domain.order.OrderResponseDto;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsResponseDto;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class OrderAdminController {

    private final OrderAdminService orderAdminService;

    // 주문 내역 검색
    @GetMapping("/orders")
    public ResponseEntity<OrderResponseDto> getOrders(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "false") boolean isAsc
    ) {
        log.info("주문 내역 검색");

        OrderResponseDto responseDto = orderAdminService.getOrders(userDetails.getUser(),
            pageable.getPageNumber(), pageable.getPageSize(), sortBy, isAsc);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 주문 상세 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDetailsResponseDto> getOrderDetails(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID orderId) {
        log.info("주문 상세 조회 요청 - orderId : {}", orderId);

        OrderDetailsResponseDto responseDto = orderAdminService.getOrderDetails(
            userDetails.getUser(),
            orderId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 아래 삭제 예정 : 혹시 몰라 수정 마치기 전까지 남겨두겠습니다

//    // 주문 상태 변경
//    @PutMapping("/orders/{orderId}/status")
//    public ResponseEntity<OrderResponseDto> updateOrderStatus(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable UUID orderId,
//        @RequestParam OrderStatusEnum status
//    ) {
//        log.info("주문 상태 변경 : {}", orderId);
//
//        OrderResponseDto orderResponseDto = orderAdminService.updateOrderStatus(orderId, status,
//            userDetails.getUser());
//        return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);
//    }
//
//    // 주문 취소
//    @PutMapping("/orders/{orderId}/cancel")
//    public ResponseEntity<String> canceledOrder(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable UUID orderId
//    ) {
//        log.info("주문 취소 요청 - orderId : {}", orderId);
//
//        return orderAdminService.canceledOrder(userDetails.getUser(), orderId);
//    }

}
