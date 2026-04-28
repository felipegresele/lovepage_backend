package com.loveapp.love_app_backend.services;

import com.loveapp.love_app_backend.modal.dtos.PixPaymentResponseDTO;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
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

    // ─────────────────────────────────────────────────────────────────
    // MERCADO PAGO — Preference (cartão + boleto)
    // ─────────────────────────────────────────────────────────────────

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

        // Exclui PIX e débito — só cartão de crédito e boleto
        PreferencePaymentTypeRequest debitCard =
                PreferencePaymentTypeRequest.builder().id("debit_card").build();
        PreferencePaymentTypeRequest prepaidCard =
                PreferencePaymentTypeRequest.builder().id("prepaid_card").build();
        PreferencePaymentTypeRequest bankTransfer =
                PreferencePaymentTypeRequest.builder().id("bank_transfer").build();

        PreferencePaymentMethodsRequest paymentMethods =
                PreferencePaymentMethodsRequest.builder()
                        .excludedPaymentTypes(List.of(debitCard, prepaidCard, bankTransfer))
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

    // ─────────────────────────────────────────────────────────────────
    // PIX — Payment direto (gera QR Code imediatamente)
    // ─────────────────────────────────────────────────────────────────

    /**
     * Cria um pagamento PIX via API do Mercado Pago e retorna o QR Code.
     *
     * @param amount     valor em BRL
     * @param payerEmail email do comprador (obrigatório pelo MP)
     * @param pageId     referência externa para o webhook associar à página
     */
    public PixPaymentResponseDTO createPixPayment(BigDecimal amount, String payerEmail, UUID pageId) throws Exception {

        MercadoPagoConfig.setAccessToken(token);

        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(amount)
                .description("Página romântica personalizada - HeartCode")
                .paymentMethodId("pix")
                .externalReference(pageId.toString())
                .notificationUrl("https://lovepage-backend.onrender.com/api/payment/webhook")
                .payer(
                        PaymentPayerRequest.builder()
                                .email(payerEmail)
                                .build()
                )
                .build();

        PaymentClient client = new PaymentClient();
        Payment payment = client.create(request);

        log.info("[PIX] Pagamento PIX criado - id={} status={}", payment.getId(), payment.getStatus());

        String qrCode = payment.getPointOfInteraction()
                .getTransactionData().getQrCode();
        String qrCodeBase64 = payment.getPointOfInteraction()
                .getTransactionData().getQrCodeBase64();

        return new PixPaymentResponseDTO(
                payment.getId(),
                qrCode,
                qrCodeBase64,
                payment.getStatus()
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // Utilitários — usados pelo webhook
    // ─────────────────────────────────────────────────────────────────

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

    /**
     * Retorna o status atual de um pagamento PIX (para polling do frontend).
     * Possíveis valores: "pending", "approved", "rejected", "cancelled"
     */
    public String getPixPaymentStatus(Long paymentId) throws Exception {
        MercadoPagoConfig.setAccessToken(token);
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);
        return payment.getStatus();
    }
}