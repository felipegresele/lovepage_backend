package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment() throws Exception {

        String checkout = paymentService.createPayment(
                new BigDecimal("17"),
                "Página romântica personalizada"
        );

        return ResponseEntity.ok(checkout);

    }

}
