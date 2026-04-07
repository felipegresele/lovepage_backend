package com.loveapp.love_app_backend.repositories;

import com.loveapp.love_app_backend.modal.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}