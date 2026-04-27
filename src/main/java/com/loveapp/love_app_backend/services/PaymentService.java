package com.loveapp.love_app_backend.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${mercadopago.token}")
    private String token;

    public String createPayment(BigDecimal amount, String title, String notificationUrl, UUID pageId) throws Exception {

        MercadoPagoConfig.setAccessToken(token);

        PreferenceItemRequest item =
                PreferenceItemRequest.builder()
                        .title(title)
                        .quantity(1)
                        .currencyId("BRL")
                        .unitPrice(amount)
                        .build();

        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success("https://www.heartcodegift.com.br/sucesso")
                        .failure("https://www.heartcodegift.com.br/erro")
                        .pending("https://www.heartcodegift.com.br/pendente")
                        .build();

        PreferencePaymentTypeRequest debitCard =
                PreferencePaymentTypeRequest.builder()
                        .id("debit_card")
                        .build();

        PreferencePaymentTypeRequest prepaidCard =
                PreferencePaymentTypeRequest.builder()
                        .id("prepaid_card")
                        .build();

        PreferencePaymentMethodsRequest paymentMethods =
                PreferencePaymentMethodsRequest.builder()
                        .excludedPaymentTypes(List.of(debitCard, prepaidCard))
                        .installments(1)
                        .build();

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .backUrls(backUrls)
                        .autoReturn("approved")
                        .notificationUrl(notificationUrl)
                        .externalReference(pageId.toString())
                        .paymentMethods(paymentMethods)
                        .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        log.info("[PAYMENT] Preferencia criada - id={} externalReference={}", preference.getId(), pageId);

        return preference.getInitPoint() + "|" + preference.getId();
    }

    public boolean isPaymentApproved(Long paymentId) throws Exception {
        MercadoPagoConfig.setAccessToken(token);
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);
        return "approved".equals(payment.getStatus());
    }

    public String getPageIdByPaymentId(Long paymentId) throws Exception {
        MercadoPagoConfig.setAccessToken(token);
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);
        log.info("[PAYMENT] externalReference={}", payment.getExternalReference());
        return payment.getExternalReference();
    }
}