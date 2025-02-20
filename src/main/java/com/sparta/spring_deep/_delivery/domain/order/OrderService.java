package com.sparta.spring_deep._delivery.domain.order;

import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.address.repository.AddressRepository;
import com.sparta.spring_deep._delivery.domain.menu.Menu;
import com.sparta.spring_deep._delivery.domain.menu.MenuRepository;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsRequestDto;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsResponseDto;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItem;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItemRepository;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private final MenuRepository menuRepository;
    private LocalDateTime lastCheckedTime = LocalDateTime.now().minusMinutes(5);


    // 주문 생성
    public OrderDetailsResponseDto createOrder(OrderDetailsRequestDto requestDto, User user) {

        if (!user.getRole().equals(UserRole.CUSTOMER)) {
            throw new IllegalStateException("Only customer can create order");
        }

        Restaurant restaurant = restaurantRepository.findById(requestDto.getRestaurantId())
            .orElseThrow(() -> new IllegalArgumentException("음식점을 찾을 수 없습니다."));

        Address address = addressRepository.findById(requestDto.getAddressId())
            .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));

        Order order = orderRepository.save(new Order(user, restaurant, address,
            requestDto.getTotalPrice(), requestDto.getRequest()));

        List<OrderItem> orderItemList = new ArrayList<>();

        AtomicReference<BigDecimal> sumPrice = new AtomicReference<>(BigDecimal.ZERO);

        requestDto.getOrderItemDtos().forEach(orderItemDto -> {
            Menu menu = menuRepository.findById(orderItemDto.getMenuId())
                .orElseThrow(() -> new EntityExistsException("메뉴를 찾을 수 없습니다."));

            BigDecimal itemPrice = Objects.requireNonNullElse(orderItemDto.getPrice(),
                BigDecimal.ZERO);
            sumPrice.updateAndGet(current ->
                current.add(itemPrice.multiply(BigDecimal.valueOf(orderItemDto.getQuantity())))
            );

            OrderItem orderItem = orderItemRepository.save(
                new OrderItem(order, menu, orderItemDto.getQuantity(), orderItemDto.getPrice()));

            orderItemList.add(orderItem);
        });
        order.setTotalPrice(sumPrice.get());

        return new OrderDetailsResponseDto(order, orderItemList);
    }


    // 주문 상태 변경 - OWNER & MANAGER
    @Transactional
    public OrderResponseDto updateOrderStatus(UUID orderId, OrderStatusEnum status, User owner) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityExistsException("order not found"));

        if (!(owner.getRole().equals(UserRole.OWNER) || owner.getRole().equals(UserRole.MANAGER))) {
            throw new IllegalStateException("Only owner can update order status");
        }

        if (order.getStatus().equals(OrderStatusEnum.CANCELLED)) {
            throw new IllegalStateException("Already cancelled order");
        }

        order.updateOrderStatus(owner, status);

        return new OrderResponseDto(order);
    }


    // 주문 상세 조회
    @Transactional(readOnly = true)
    public OrderDetailsResponseDto getOrderDetails(User user, UUID orderId) {
        if (!(user.getRole().equals(UserRole.CUSTOMER)
            || user.getRole().equals(UserRole.OWNER)
            || user.getRole().equals(UserRole.MANAGER))) {
            throw new IllegalStateException("Only customer & owner can get order");
        }
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityExistsException("order not found"));

        if (user.getRole().equals(UserRole.CUSTOMER)
            && !order.getCustomer().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException("Unauthorized access to order");
        }

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(order.getId());

        return new OrderDetailsResponseDto(order, orderItemList);
    }

    // 나의 주문 내역 조회
    @Transactional(readOnly = true)
    public OrderResponseDto getMyOrders(User user,
        int page, int size,
        String sortBy, boolean isAsc) {

        if (!user.getRole().equals(UserRole.CUSTOMER)) {
            throw new IllegalStateException("Only customer can read order");
        }

        Sort sort = Sort.by(isAsc ? Direction.ASC : Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> myOrderList = orderRepository.findAllByCustomerUsernameAndIsDeletedFalse(
            user.getUsername(),
            pageable);

        return myOrderList.map(OrderResponseDto::new).stream().findFirst().orElse(null);
    }

    // 주문 취소 (5분 이내)
    @Transactional
    public ResponseEntity<String> canceledOrder(User user, UUID orderId) {
        if (!(user.getRole().equals(UserRole.CUSTOMER)
            || user.getRole().equals(UserRole.OWNER)
            || user.getRole().equals(UserRole.MANAGER))) {
            throw new AccessDeniedException("Unauthorized access to canceled order");
        }
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityExistsException("order not found"));

        if (user.getRole().equals(UserRole.CUSTOMER)
            && !order.getCustomer().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException("Unauthorized access to canceled order");
        }

        if (order.getStatus().equals(OrderStatusEnum.CANCELLED)) {
            throw new IllegalStateException("Already cancelled order");
        }

        if (order.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
            order.updateOrderStatus(user, OrderStatusEnum.CANCELLED);
        } else {
            throw new IllegalStateException("주문 취소 가능 시간이 초과하였습니다.");
        }
        return ResponseEntity.ok("Success canceled");
    }


    // 실시간 주문 확인
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUpdatedOrdersSince(User user) {

        // 진행 중인 주문 중에서 최근 변경된 주문만 조회
        List<Order> updatedOrders = orderRepository.findByCustomerUsernameAndUpdatedAtAfterAndStatusIn(
            user.getUsername(), lastCheckedTime,
            List.of(OrderStatusEnum.PENDING, OrderStatusEnum.CONFIRMED));

        if (updatedOrders.isEmpty()) {
            throw new EntityExistsException("현재 진행중인 주문이 없습니다.");
        }

        lastCheckedTime = LocalDateTime.now(); // 마지막 조회 시간 갱신

        return updatedOrders.stream().map(OrderResponseDto::new).toList();
    }

    // 주문 내역 삭제
    @Transactional
    public ResponseEntity<String> deletedOrder(User user, UUID orderId) {

        if (!user.getRole().equals(UserRole.CUSTOMER)) {
            throw new IllegalStateException("Only customer can delete order");
        }

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getCustomer().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException("Unauthorized access to deleted order");
        }

        if (order.getIsDeleted()) {
            throw new IllegalStateException("Already deleted order");
        }
        order.delete(user.getUsername());

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(order.getId());
        for (OrderItem orderItem : orderItemList) {
            orderItem.delete(user.getUsername());
        }
        return ResponseEntity.ok("Success deleted");
    }
}
