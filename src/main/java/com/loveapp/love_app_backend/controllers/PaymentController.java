package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.dtos.CreatePaymentDTO;
import com.loveapp.love_app_backend.services.EmailService;
import com.loveapp.love_app_backend.services.PageService;
import com.loveapp.love_app_backend.services.PaymentService;
import com.loveapp.love_app_backend.services.QRCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final PageService pageService;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    public PaymentController(PaymentService paymentService, PageService pageService, QRCodeService qrCodeService, EmailService emailService) {
        this.paymentService = paymentService;
        this.pageService = pageService;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentDTO dto) throws Exception {

        log.info("[PAYMENT] Criando pagamento - pageId={} planType={}", dto.getPageId(), dto.getPlanType());

        // Pega o valor do plano
        BigDecimal amount = dto.getPlanType().getPrice();

        // Cria o pagamento no Mercado Pago
        String result = paymentService.createPayment(amount, "Página romântica personalizada", "https://lovepage-backend.onrender.com/api/payment/webhook");

        // result = "initPoint|preferenceId"
        String[] parts = result.split("\\|");
        String initPoint = parts[0];
        String preferenceId = parts[1];

        // Salva o preferenceId na página para acompanhar depois
        pageService.savePaymentId(dto.getPageId(), preferenceId);
        log.info("[PAYMENT] Pagamento criado - preferenceId={}", preferenceId);

        return ResponseEntity.ok(initPoint);
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> paymentWebhook(
            @RequestParam(value = "type", required = false) String type,
            @RequestBody Map<String, Object> payload) throws Exception {

        log.info("[WEBHOOK] Recebido - type={} payload={}", type, payload);

        if (!"payment".equals(type)) {
            log.info("[WEBHOOK] Tipo ignorado: {}", type);
            return ResponseEntity.ok("Ignored");
        }

        // O payload vem como: { "data": { "id": "12345" } }
        Map<String, Object> dataMap = (Map<String, Object>) payload.get("data");
        Long paymentId = Long.parseLong(dataMap.get("id").toString());

        if (paymentService.isPaymentApproved(paymentId)) {
            // Busca a página pelo paymentId (preferenceId salvo antes)
            // Precisa buscar via paymentService ou direto pelo preferenceId do webhook
            // O MP envia o preference_id no payload também
            log.info("[WEBHOOK] Pagamento aprovado! id={}", paymentId);
            String preferenceId = dataMap.containsKey("preference_id")
                    ? dataMap.get("preference_id").toString()
                    : null;

            log.info("[WEBHOOK] preferenceId={}", preferenceId);

            if (preferenceId == null) {
                log.warn("[WEBHOOK] preference_id nulo no payload!");
                return ResponseEntity.ok("No preference_id");
            }

            Page page = pageService.getByPaymentId(preferenceId);

            byte[] qrCode = qrCodeService.generate("https://heartlink.com/p/" + page.getSlug());
            emailService.sendEmailWithQRCode(page.getUser().getEmail(), page.getUser().getUsername(), qrCode);
            pageService.markAsPaid(page.getId());
        }

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/simulate-paid/{pageId}")
    public ResponseEntity<?> simulatePaid(@PathVariable UUID pageId) throws Exception {
        log.info("[SIMULATE] Iniciando simulacao para pageId={}", pageId);
        try {
            Page page = pageService.getById(pageId);
            log.info("[SIMULATE] Pagina encontrada - slug={} email={}", page.getSlug(), page.getUser().getEmail());

            byte[] qrCode = qrCodeService.generate("https://heartlink-85i3.vercel.app/p/" + page.getSlug());
            log.info("[SIMULATE] QR code gerado");

            emailService.sendEmailWithQRCode(page.getUser().getEmail(), page.getUser().getUsername(), qrCode);
            log.info("[SIMULATE] E-mail enviado para {}", page.getUser().getEmail());

            pageService.markAsPaid(page.getId());
            log.info("[SIMULATE] Pagina marcada como PAGA");

            return ResponseEntity.ok("Página marcada como PAGA e e-mail enviado para: " + page.getUser().getEmail());
        } catch (Exception e) {
            log.error("[SIMULATE] Erro: {}", e.getMessage(), e);
            throw e;
        }
    }

}