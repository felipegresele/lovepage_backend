package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.dtos.CreatePaymentDTO;
import com.loveapp.love_app_backend.services.EmailService;
import com.loveapp.love_app_backend.services.PageService;
import com.loveapp.love_app_backend.services.PaymentService;
import com.loveapp.love_app_backend.services.QRCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final PageService pageService;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    // Chave secreta do webhook MP — configure no Render como MERCADOPAGO_WEBHOOK_SECRET
    @Value("${mercadopago.webhook-secret}")
    private String webhookSecret;

    public PaymentController(PaymentService paymentService, PageService pageService,
                             QRCodeService qrCodeService, EmailService emailService) {
        this.paymentService = paymentService;
        this.pageService = pageService;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    // Protegida por JWT (via SecurityConfig)
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentDTO dto) throws Exception {
        log.info("[PAYMENT] Criando pagamento - pageId={} planType={}", dto.getPageId(), dto.getPlanType());

        BigDecimal amount = dto.getTotalAmount() != null ? dto.getTotalAmount() : dto.getPlanType().getPrice();

        pageService.saveQrCodeFrame(dto.getPageId(), dto.getQrCodeFrame());

        String result = paymentService.createPayment(
                amount,
                "Página romântica personalizada - HeartCode",
                "https://lovepage-backend.onrender.com/api/payment/webhook",
                dto.getPageId()
        );

        String[] parts = result.split("\\|");
        String initPoint = parts[0];
        String preferenceId = parts[1];

        pageService.savePaymentId(dto.getPageId(), preferenceId);
        log.info("[PAYMENT] Pagamento criado - preferenceId={}", preferenceId);

        return ResponseEntity.ok(initPoint);
    }

    // Público — chamado pelo Mercado Pago, mas com validação de assinatura HMAC
    @PostMapping("/webhook")
    public ResponseEntity<?> paymentWebhook(
            @RequestParam(value = "type", required = false) String type,
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestBody Map<String, Object> payload) throws Exception {

        log.info("[WEBHOOK] Recebido - type={}", type);

        // Valida assinatura HMAC antes de processar qualquer coisa
        if (!isValidSignature(xSignature, xRequestId, dataId)) {
            log.warn("[WEBHOOK] Assinatura inválida! Possível requisição falsa.");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!"payment".equals(type)) {
            log.info("[WEBHOOK] Tipo ignorado: {}", type);
            return ResponseEntity.ok("Ignored");
        }

        Map<String, Object> dataMap = (Map<String, Object>) payload.get("data");
        Long paymentId = Long.parseLong(dataMap.get("id").toString());
        log.info("[WEBHOOK] Verificando pagamento id={}", paymentId);

        if (paymentService.isPaymentApproved(paymentId)) {
            log.info("[WEBHOOK] Pagamento aprovado! id={}", paymentId);

            String pageIdStr = paymentService.getPageIdByPaymentId(paymentId);
            log.info("[WEBHOOK] pageId recuperado={}", pageIdStr);

            if (pageIdStr == null) {
                log.warn("[WEBHOOK] externalReference nulo!");
                return ResponseEntity.ok("No externalReference");
            }

            Page page = pageService.getById(UUID.fromString(pageIdStr));
            log.info("[WEBHOOK] Pagina encontrada - slug={} email={}", page.getSlug(), page.getUser().getEmail());

            String pageUrl = "https://heartlink-85i3.vercel.app/p/" + page.getSlug();
            byte[] qrCode = qrCodeService.generateWithFrame(pageUrl, page.getQrCodeFrame());

            emailService.sendEmailWithQRCode(page.getUser().getEmail(), page.getUser().getUsername(), qrCode);
            pageService.markAsPaid(page.getId());
            log.info("[WEBHOOK] Pagina marcada como PAGA e email enviado!");
        } else {
            log.warn("[WEBHOOK] Pagamento NAO aprovado - id={}", paymentId);
        }

        return ResponseEntity.ok("OK");
    }

    // Valida assinatura HMAC-SHA256 conforme documentação do Mercado Pago
    private boolean isValidSignature(String xSignature, String xRequestId, String dataId) {
        if (xSignature == null || webhookSecret == null || webhookSecret.isBlank()) {
            return false;
        }

        try {
            // Extrai ts e v1 do header x-signature
            String ts = null;
            String v1 = null;
            for (String part : xSignature.split(",")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2) {
                    if ("ts".equals(kv[0])) ts = kv[1];
                    if ("v1".equals(kv[0])) v1 = kv[1];
                }
            }

            if (ts == null || v1 == null) return false;

            // Monta o template conforme documentação MP
            String manifest = "id:" + dataId + ";request-id:" + xRequestId + ";ts:" + ts + ";";

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(manifest.getBytes());
            String computed = HexFormat.of().formatHex(hash);

            return computed.equals(v1);
        } catch (Exception e) {
            log.error("[WEBHOOK] Erro ao validar assinatura: {}", e.getMessage());
            return false;
        }
    }

    // ❌ simulate-paid REMOVIDO — era uma brecha crítica de segurança
}
