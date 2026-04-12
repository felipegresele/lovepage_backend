package com.loveapp.love_app_backend.modal.dtos;

import com.loveapp.love_app_backend.modal.types.PlanType;
import com.loveapp.love_app_backend.modal.types.QrCodeFrame;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDTO {
    private UUID pageId;
    private PlanType planType;
    private QrCodeFrame qrCodeFrame;
    private BigDecimal totalAmount;
}