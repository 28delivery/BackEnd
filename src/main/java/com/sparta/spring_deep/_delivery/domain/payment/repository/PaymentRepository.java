package com.sparta.spring_deep._delivery.domain.payment.repository;

import com.sparta.spring_deep._delivery.domain.payment.model.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
}
