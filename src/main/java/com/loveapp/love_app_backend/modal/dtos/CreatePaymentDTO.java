package com.loveapp.love_app_backend.modal.dtos;

import com.loveapp.love_app_backend.modal.types.PlanType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDTO {
    private UUID pageId;
    private PlanType planType;
}