package com.loveapp.love_app_backend.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

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
}