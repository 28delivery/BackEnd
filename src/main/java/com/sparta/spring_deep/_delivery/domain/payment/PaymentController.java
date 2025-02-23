package com.sparta.spring_deep._delivery.domain.payment;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 생성
    @PostMapping("/payment")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequestDto requestDto
        , @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Payment payment = paymentService.createPayment(userDetails.getUsername(),
            UUID.fromString(requestDto.orderId),
            requestDto.amount);
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    // 결제 완료 처리
    @PutMapping("/payment/complete")
    public ResponseEntity<Payment> completePayment(@RequestParam String paymentId) {
        
        Payment payment = paymentService.completePayment(UUID.fromString(paymentId));
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    // 결제 취소 처리
    @PutMapping("/payment/cancel")
    public ResponseEntity<Payment> cancelPayment(@RequestParam String paymentId) {

        Payment payment = paymentService.cancelPayment(UUID.fromString(paymentId));
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    // 결제 실패 처리
    @PutMapping("/payment/fail")
    public ResponseEntity<Payment> failPayment(@RequestParam String paymentId) {

        Payment payment = paymentService.failPayment(UUID.fromString(paymentId));
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    // 결제 조회
    @GetMapping("/payment")
    public ResponseEntity<Payment> getPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam String paymentId) {

        Payment payment = paymentService.getPayment(userDetails.getUser(),
            UUID.fromString(paymentId));
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    // 결제 내역 삭제
    @DeleteMapping("/payment")
    public ResponseEntity<String> deletePayment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam String paymentId) {

        return paymentService.deletPayment(userDetails.getUser(), UUID.fromString(paymentId));
    }


}
