package com.sparta.spring_deep._delivery.domain.payment;

import static com.sparta.spring_deep._delivery.util.AuthTools.ownerCheck;

import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Payment Service")
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // 결제 생성
    @Transactional
    public Payment createPayment(String username, UUID orderId, BigDecimal amount) {
        log.info("Creating Payment for username {} and order id {}", username, orderId);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(ResourceNotFoundException::new);

        Payment payment = new Payment(username, order, amount);
        return paymentRepository.save(payment);
    }

    // 결제 완료 처리
    @Transactional
    public Payment completePayment(UUID paymentId) {
        log.info("Completing Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        payment.completePayment();
        return payment;
    }

    // 결제 취소 처리
    @Transactional
    public Payment cancelPayment(UUID paymentId) {
        log.info("Canceling Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        payment.cancelPayment();
        return payment;
    }

    // 결제 실패 처리
    @Transactional
    public Payment failPayment(UUID paymentId) {
        log.info("Failing Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        payment.failPayment();
        return payment;
    }

    // 결제 조회
    @Transactional(readOnly = true)
    public Payment getPayment(User user, UUID paymentId) {
        log.info("Getting Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        // 결제자만 결제 조회 가능
        ownerCheck(user, payment.getOrder().getCustomer());

        return payment;
    }

    // 결제 내역 삭제
    @Transactional
    public ResponseEntity<String> deletPayment(User user, UUID paymentId) {
        log.info("Deleting Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        // 결제자만 결제 내역 삭제 가능
        ownerCheck(user, payment.getOrder().getCustomer());

        payment.delete(user.getUsername());
        return ResponseEntity.status(HttpStatus.OK)
            .body("Successfully soft deleted payment with id: " + paymentId);
    }
}
