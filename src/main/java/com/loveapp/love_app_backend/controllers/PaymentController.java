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

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

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

        return ResponseEntity.ok(initPoint);
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> paymentWebhook(
            @RequestParam(value = "type", required = false) String type,
            @RequestBody Map<String, Object> payload) throws Exception {

        if (!"payment".equals(type)) {
            return ResponseEntity.ok("Ignored");
        }

        // O payload vem como: { "data": { "id": "12345" } }
        Map<String, Object> dataMap = (Map<String, Object>) payload.get("data");
        Long paymentId = Long.parseLong(dataMap.get("id").toString());

        if (paymentService.isPaymentApproved(paymentId)) {
            // Busca a página pelo paymentId (preferenceId salvo antes)
            // Precisa buscar via paymentService ou direto pelo preferenceId do webhook
            // O MP envia o preference_id no payload também
            String preferenceId = dataMap.containsKey("preference_id")
                    ? dataMap.get("preference_id").toString()
                    : null;

            if (preferenceId == null) return ResponseEntity.ok("No preference_id");

            Page page = pageService.getByPaymentId(preferenceId);

            byte[] qrCode = qrCodeService.generate("https://heartlink.com/p/" + page.getSlug());
            emailService.sendEmailWithQRCode(page.getUser().getEmail(), page.getUser().getUsername(), qrCode);
            pageService.markAsPaid(page.getId());
        }

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/simulate-paid/{pageId}")
    public ResponseEntity<?> simulatePaid(@PathVariable UUID pageId) throws Exception {
        Page page = pageService.getById(pageId);

        byte[] qrCode = qrCodeService.generate("https://heartlink-85i3.vercel.app/p/" + page.getSlug());
        emailService.sendEmailWithQRCode(page.getUser().getEmail(), page.getUser().getUsername(), qrCode);
        pageService.markAsPaid(page.getId());

        return ResponseEntity.ok("Página marcada como PAGA e e-mail enviado para: " + page.getUser().getEmail());
    }

}