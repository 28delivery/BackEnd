package com.sparta.spring_deep._delivery.domain.payment;

import static com.sparta.spring_deep._delivery.util.AuthTools.ownerCheck;

import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import com.sparta.spring_deep._delivery.exception.InternalServerException;
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
@Slf4j(topic = "PaymentService")
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // 결제 생성
    @Transactional
    public PaymentResponseDto createPayment(String username, UUID orderId, BigDecimal amount) {
        log.info("Creating Payment for username {} and order id {}", username, orderId);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(ResourceNotFoundException::new);

        Payment payment = new Payment(username, order, amount);

        // 카드사 요청 대기
        try {
            Thread.sleep(1000);
            boolean paymentSuccess;

            // 90% 확률로 COMPLETE
            // 10% 확률로 FAILED
            if (Math.random() <= 0.9) {
                paymentSuccess = true;
            } else {
                paymentSuccess = false;
            }

            if (paymentSuccess) {
                // complete
                payment.completePayment();
            } else {
                // failed
                payment.failPayment();
            }

        } catch (InterruptedException e) {
            throw new InternalServerException();
        }

        paymentRepository.save(payment);
        return new PaymentResponseDto(payment);
    }

    // 결제 완료 처리
    @Transactional
    public PaymentResponseDto completePayment(UUID paymentId) {
        log.info("Completing Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        payment.completePayment();

        return new PaymentResponseDto(payment);
    }

    // 결제 취소 처리
    @Transactional
    public PaymentResponseDto cancelPayment(UUID paymentId) {
        log.info("Canceling Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        payment.cancelPayment();
        return new PaymentResponseDto(payment);
    }

    // 결제 실패 처리
    @Transactional
    public PaymentResponseDto failPayment(UUID paymentId) {
        log.info("Failing Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        payment.failPayment();
        return new PaymentResponseDto(payment);
    }

    // 결제 조회
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(User user, UUID paymentId) {
        log.info("Getting Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        // 결제자만 결제 조회 가능
        if (!user.getRole().equals(UserRole.ADMIN)) {
            ownerCheck(user, payment.getOrder().getCustomer());
        }

        return new PaymentResponseDto(payment);
    }

    // 결제 내역 삭제
    @Transactional
    public ResponseEntity<String> deletPayment(User user, UUID paymentId) {
        log.info("Deleting Payment for payment id {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(ResourceNotFoundException::new);

        // 결제자만 결제 내역 삭제 가능
        if (!user.getRole().equals(UserRole.ADMIN)) {
            ownerCheck(user, payment.getOrder().getCustomer());
        }

        payment.delete(user.getUsername());
        return ResponseEntity.status(HttpStatus.OK)
            .body("Successfully soft deleted payment with id: " + paymentId);
    }
}
