package com.loveapp.love_app_backend.modal.types;

import java.math.BigDecimal;

public enum PlanType {
    PADRAO("Plano Padrão", new BigDecimal("20")),
    VITALICIO("Vitalício", new BigDecimal("25"));

    private final String title;
    private final BigDecimal price;

    PlanType(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
}
