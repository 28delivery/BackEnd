package com.sparta.spring_deep._delivery.admin.order;

import com.sparta.spring_deep._delivery.admin.order.orderItem.OrderItemAdminRepository;
import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderResponseDto;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsResponseDto;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItem;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderAdminService {

    private final OrderAdminRepository orderAdminRepository;
    private final OrderItemAdminRepository orderItemAdminRepository;

    // 주문 내역 검색
    @Transactional(readOnly = true)
    public OrderResponseDto getOrders(User user, int page, int size, String sortBy,
        boolean isAsc) {

        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new IllegalStateException("Only admin can get orders");
        }

        Sort sort = Sort.by(isAsc ? Direction.ASC : Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> ownerOrderList = orderAdminRepository.findAllByCustomerUsername(
            user.getUsername(),
            pageable);

        return ownerOrderList.map(OrderResponseDto::new).stream().findFirst().orElse(null);

    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    public OrderDetailsResponseDto getOrderDetails(User user, UUID orderId) {
        if (!(user.getRole().equals(UserRole.ADMIN))) {
            throw new IllegalStateException("Only admin can get order details");
        }
        Order order = orderAdminRepository.findById(orderId)
            .orElseThrow(() -> new EntityExistsException("order not found"));

        List<OrderItem> orderItemList = orderItemAdminRepository.findAllByOrderId(
            order.getId());

        return new OrderDetailsResponseDto(order, orderItemList);
    }

    public Page<OrderResponseDto> searchOrders(OrderAdminSearchDto searchDto, Pageable pageable) {
        Page<OrderResponseDto> responseDtos = orderAdminRepository.searchByOption(searchDto,
            pageable);

        // 검색 결과 비었으면 Exception 반환
        if (responseDtos.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return responseDtos;
    }

//    // 주문 상태 변경
//    @Transactional
//    public OrderResponseDto updateOrderStatus(UUID orderId, OrderStatusEnum status, User user) {
//        if (!user.getRole().equals(UserRole.ADMIN)) {
//            throw new IllegalStateException("Only admin can update order status");
//        }
//
//        Order order = orderAdminRepositoryRepository.findById(orderId)
//            .orElseThrow(() -> new EntityExistsException("order not found"));
//
//        if (order.getStatus().equals(OrderStatusEnum.CANCELLED)) {
//            throw new IllegalStateException("Already cancelled order");
//        }
//
//        if (order.getStatus().equals(status)) {
//            throw new IllegalStateException("Already changed status");
//        }
//
//        order.updateOrderStatus(user, status);
//
//        return new OrderResponseDto(order);
//    }
//
//    // 주문 취소
//    @Transactional
//    public ResponseEntity<String> canceledOrder(User user, UUID orderId) {
//        if (!user.getRole().equals(UserRole.ADMIN)) {
//            throw new IllegalStateException("Only admin can cancel order");
//        }
//
//        Order order = orderAdminRepositoryRepository.findById(orderId)
//            .orElseThrow(() -> new EntityExistsException("order not found"));
//
//        if (order.getStatus().equals(OrderStatusEnum.CANCELLED)) {
//            throw new IllegalStateException("Already cancelled order");
//        }
//
//        order.updateOrderStatus(user, OrderStatusEnum.CANCELLED);
//
//        return ResponseEntity.ok("success Cancelled");
//    }
}
