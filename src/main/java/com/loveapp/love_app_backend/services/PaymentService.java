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
                        .success("https://heartlink-85i3.vercel.app/sucesso")
                        .failure("https://heartlink-85i3.vercel.app/erro")
                        .pending("https://heartlink-85i3.vercel.app/pendente")
                        .build();

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .backUrls(backUrls)
                        .notificationUrl(notificationUrl)
                        .externalReference(pageId.toString()) // salva o pageId para recuperar no webhook
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
        return payment.getExternalReference(); // retorna o pageId que salvamos
    }
}