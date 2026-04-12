package com.loveapp.love_app_backend.services;

import com.loveapp.love_app_backend.controllers.PaymentController;
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

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${mercadopago.token}")
    private String token;

    public String createPayment(BigDecimal amount, String title, String notificationUrl) throws Exception {

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
                        .success("https://heartlink.com/sucesso")
                        .failure("https://heartlink.com/erro")
                        .pending("https://heartlink.com/pendente")
                        .build();

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .backUrls(backUrls)
                        .notificationUrl(notificationUrl)
                        .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // Retorna tanto o initPoint quanto o preferenceId separados
        return preference.getInitPoint() + "|" + preference.getId();
    }

    public boolean isPaymentApproved(Long paymentId) throws Exception {
        MercadoPagoConfig.setAccessToken(token);

        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);

        return "approved".equals(payment.getStatus());
    }

    public String getPreferenceIdByPaymentId(Long paymentId) throws Exception {
        MercadoPagoConfig.setAccessToken(token);
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);

        // Loga todos os dados disponíveis para descobrir o campo certo
        log.info("[PAYMENT] status={}", payment.getStatus());
        log.info("[PAYMENT] externalReference={}", payment.getExternalReference());
        log.info("[PAYMENT] metadata={}", payment.getMetadata());
        log.info("[PAYMENT] additionalInfo={}", payment.getAdditionalInfo());

        return null; // temporario
    }
}