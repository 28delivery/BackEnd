package com.sparta.spring_deep._delivery.domain.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Payment Service")
public class PaymentService {

    private final PaymentRepository paymentRepository;


}
