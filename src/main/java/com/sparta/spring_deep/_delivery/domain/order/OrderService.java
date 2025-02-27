package com.sparta.spring_deep._delivery.domain.order;

import static com.sparta.spring_deep._delivery.util.AuthTools.ownerCheck;

import com.sparta.spring_deep._delivery.domain.address.entity.Address;
import com.sparta.spring_deep._delivery.domain.address.repository.AddressRepository;
import com.sparta.spring_deep._delivery.domain.menu.Menu;
import com.sparta.spring_deep._delivery.domain.menu.MenuRepository;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsRequestDto;
import com.sparta.spring_deep._delivery.domain.order.orderDetails.OrderDetailsResponseDto;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItem;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItemRepository;
import com.sparta.spring_deep._delivery.domain.payment.Payment.PaymentStatusEnum;
import com.sparta.spring_deep._delivery.domain.payment.PaymentResponseDto;
import com.sparta.spring_deep._delivery.domain.payment.PaymentService;
import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.review.Review;
import com.sparta.spring_deep._delivery.domain.review.ReviewRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.domain.user.repository.UserRepository;
import com.sparta.spring_deep._delivery.exception.OperationNotAllowedException;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "OrderService")
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentService paymentService;
    private LocalDateTime lastCheckedTime = LocalDateTime.now().minusMinutes(5);


    // 주문 생성
    @Transactional
    public OrderDetailsResponseDto createOrder(OrderDetailsRequestDto requestDto, User user) {
        log.info("주문 생성");

        Restaurant restaurant = restaurantRepository.findByIdAndIsDeletedFalse(
                requestDto.getRestaurantId())
            .orElseThrow(ResourceNotFoundException::new);

        // ID로 주소 찾기
        Address address = addressRepository.findByIdAndIsDeletedFalse(requestDto.getAddressId())
            .orElseThrow(ResourceNotFoundException::new);

        // 유저 정보와 주소 정보 일치하는지 검사
        ownerCheck(user, address.getUser());

        Order order = orderRepository.save(new Order(user, restaurant, address,
            BigDecimal.ZERO, requestDto.getRequest()));

        AtomicReference<BigDecimal> sumPrice = new AtomicReference<>(BigDecimal.ZERO);

        List<OrderItem> orderItemList = new ArrayList<>();

        requestDto.getOrderItemDtos().forEach(orderItemDto -> {
            Menu menu = menuRepository.findById(orderItemDto.getMenuId())
                .orElseThrow(ResourceNotFoundException::new);

            BigDecimal itemPrice = menu.getPrice();
            log.info("item price : " + itemPrice);
            sumPrice.updateAndGet(current ->
                current.add(itemPrice.multiply(BigDecimal.valueOf(orderItemDto.getQuantity())))
            );

            log.info("sum Price : " + sumPrice.get());
            OrderItem orderItem = orderItemRepository.save(
                new OrderItem(order, menu, orderItemDto.getQuantity(), itemPrice));

            orderItemList.add(orderItem);
        });
        order.updateTotalPrice(sumPrice.get());

        // 결제 요청
        PaymentResponseDto paymentResponseDto = paymentService.createPayment(user.getUsername(),
            order.getId(), sumPrice.get());

        if (paymentResponseDto.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
            order.updateOrderStatus(user, OrderStatusEnum.CONFIRMED);
            
        } else if (paymentResponseDto.getPaymentStatus() == PaymentStatusEnum.FAILED) {
            order.updateOrderStatus(user, OrderStatusEnum.FAILED);
            order.delete(user.getUsername());
        }

        return new OrderDetailsResponseDto(order, orderItemList);
    }


    // 주문 상태 변경 - OWNER & MANAGER
    @Transactional
    public OrderResponseDto updateOrderStatus(UUID orderId, OrderStatusEnum status, User owner) {
        log.info("주문 상태 변경");

        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
            .orElseThrow(ResourceNotFoundException::new);

        if (owner.getRole().equals(UserRole.OWNER)) {
            ownerCheck(owner, order.getRestaurant().getOwner());
        }

        if (order.getStatus().equals(OrderStatusEnum.CANCELLED)) {
            log.error("취소된 주문 : orderId : " + orderId);
            throw new OperationNotAllowedException();
        }

        order.updateOrderStatus(owner, status);

        return new OrderResponseDto(order);
    }


    // 주문 상세 조회
    @Transactional(readOnly = true)
    public OrderDetailsResponseDto getOrderDetails(User user, UUID orderId) {
        log.info("주문 상세 조회");

        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
            .orElseThrow(ResourceNotFoundException::new);

        if (user.getRole().equals(UserRole.CUSTOMER)) {
            ownerCheck(user, order.getCustomer());
        }
        if (user.getRole().equals(UserRole.OWNER)) {
            ownerCheck(user, order.getRestaurant().getOwner());
        }

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(order.getId());

        return new OrderDetailsResponseDto(order, orderItemList);
    }

    // 나의 주문 내역 조회
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> searchMyOrders(User user, OrderSearchDto searchDto,
        Pageable pageable) {
        log.info("나의 주문 내역 조회");

        // 내 주문 내역만 조회
        Page<OrderResponseDto> myOrderResponseDto = orderRepository.searchMyOrdersByOptionAndIsDeletedFalse(
            user.getUsername(), searchDto, pageable);

        // 주문 내역이 비어있다면, Exception 발생
        if (myOrderResponseDto.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return myOrderResponseDto;
    }

    // 주문 취소 (5분 이내)
    @Transactional
    public ResponseEntity<String> canceledOrder(User user, UUID orderId) {
        log.info("주문 취소 (5분 이내)");

        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
            .orElseThrow(ResourceNotFoundException::new);

        if (user.getRole().equals(UserRole.CUSTOMER)) {
            ownerCheck(user, order.getCustomer());
        }

        if (user.getRole().equals(UserRole.OWNER)) {
            ownerCheck(user, order.getRestaurant().getOwner());
        }

        if (order.getStatus().equals(OrderStatusEnum.CANCELLED)) {
            log.info("취소된 주문입니다.");
            throw new OperationNotAllowedException();
        }

        if (order.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
            order.updateOrderStatus(user, OrderStatusEnum.CANCELLED);
        } else {
            log.info("주문 취소 가능 시간이 초과하였습니다.");
            throw new OperationNotAllowedException();
        }
        return ResponseEntity.ok("Success canceled");
    }


    // 실시간 주문 확인
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getUpdatedOrdersSince(User user, int page, int size) {
        log.info("실시간 주문 확인");

        Sort sort = Sort.by(Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        // 진행 중인 주문 중에서 최근 변경된 주문만 조회
        Page<Order> updatedOrders = orderRepository.findByCustomerUsernameAndIsDeletedFalseAndUpdatedAtAfterAndStatusIn(
            user.getUsername(), lastCheckedTime,
            List.of(OrderStatusEnum.PENDING, OrderStatusEnum.CONFIRMED),
            pageable);

        if (updatedOrders.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        lastCheckedTime = LocalDateTime.now(); // 마지막 조회 시간 갱신

        return updatedOrders.map(OrderResponseDto::new);
    }

    // 주문 내역 삭제
    @Transactional
    public ResponseEntity<String> deletedOrder(User user, UUID orderId) {
        log.info("주문 내역 삭제");

        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
            .orElseThrow(ResourceNotFoundException::new);

        if (!user.getRole().equals(UserRole.ADMIN)) {
            ownerCheck(user, order.getCustomer());
        }

        order.delete(user.getUsername());

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(order.getId());
        for (OrderItem orderItem : orderItemList) {
            orderItem.delete(user.getUsername());
        }

        List<Review> reviews = reviewRepository.findAllByOrderId(order.getId());
        for (Review review : reviews) {
            review.delete(user.getUsername());
        }

        return ResponseEntity.ok("Success deleted");
    }
}
