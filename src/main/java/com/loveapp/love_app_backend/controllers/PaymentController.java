package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.dtos.CreatePaymentDTO;
import com.loveapp.love_app_backend.services.EmailService;
import com.loveapp.love_app_backend.services.PageService;
import com.loveapp.love_app_backend.services.PaymentService;
import com.loveapp.love_app_backend.services.QRCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    public PaymentController(PaymentService paymentService, PageService pageService, QRCodeService qrCodeService, EmailService emailService) {
        this.paymentService = paymentService;
        this.pageService = pageService;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentDTO dto) throws Exception {
        log.info("[PAYMENT] Criando pagamento - pageId={} planType={}", dto.getPageId(), dto.getPlanType());

        //Usa o valor total calculado no front, se nao vier usa o valor do plano escolhido
        BigDecimal amount = dto.getTotalAmount() != null ? dto.getTotalAmount() : dto.getPlanType().getPrice();

        pageService.saveQrCodeFrame(dto.getPageId(), dto.getQrCodeFrame());

        // passa o pageId para salvar como externalReference no MP
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

    @PostMapping("/webhook")
    public ResponseEntity<?> paymentWebhook(
            @RequestParam(value = "type", required = false) String type,
            @RequestBody Map<String, Object> payload) throws Exception {

        log.info("[WEBHOOK] Recebido - type={} payload={}", type, payload);

        if (!"payment".equals(type)) {
            log.info("[WEBHOOK] Tipo ignorado: {}", type);
            return ResponseEntity.ok("Ignored");
        }

        Map<String, Object> dataMap = (Map<String, Object>) payload.get("data");
        Long paymentId = Long.parseLong(dataMap.get("id").toString());
        log.info("[WEBHOOK] Verificando pagamento id={}", paymentId);

        if (paymentService.isPaymentApproved(paymentId)) {
            log.info("[WEBHOOK] Pagamento aprovado! id={}", paymentId);

            // busca o pageId pelo externalReference salvo no MP
            String pageIdStr = paymentService.getPageIdByPaymentId(paymentId);
            log.info("[WEBHOOK] pageId recuperado={}", pageIdStr);

            if (pageIdStr == null) {
                log.warn("[WEBHOOK] externalReference nulo!");
                return ResponseEntity.ok("No externalReference");
            }

            Page page = pageService.getById(UUID.fromString(pageIdStr));
            log.info("[WEBHOOK] Pagina encontrada - slug={} email={}", page.getSlug(), page.getUser().getEmail());

            //Gera o QR Code e aplica a moldura se tiver
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

    @PostMapping("/simulate-paid/{pageId}")
    public ResponseEntity<?> simulatePaid(@PathVariable UUID pageId) throws Exception {
        log.info("[SIMULATE] Iniciando simulacao para pageId={}", pageId);
        try {
            Page page = pageService.getById(pageId);
            log.info("[SIMULATE] Pagina encontrada - slug={} email={}", page.getSlug(), page.getUser().getEmail());

            String pageUrl = "https://heartlink-85i3.vercel.app/p/" + page.getSlug();
            byte[] qrCode = qrCodeService.generateWithFrame(pageUrl, page.getQrCodeFrame());
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