package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.dtos.CreatePaymentDTO;
import com.loveapp.love_app_backend.services.EmailService;
import com.loveapp.love_app_backend.services.PageService;
import com.loveapp.love_app_backend.services.PaymentService;
import com.loveapp.love_app_backend.services.QRCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

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
        String checkout = paymentService.createPayment(amount, "Página romântica personalizada");

        // Salva o preferenceId (checkout) na página para acompanhar depois
        pageService.savePaymentId(dto.getPageId(), checkout);

        return ResponseEntity.ok(checkout);
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> paymentWebhook(@RequestBody Map<String, Object> payload) throws Exception {
        // O ID do pagamento vem como número
        Long paymentId = Long.parseLong(payload.get("data.id").toString());

        // Verifica se pagamento foi aprovado
        if(paymentService.isPaymentApproved(paymentId)) {
            // Busca a página associada pelo preferenceId
            String preferenceId = payload.get("preference_id").toString();
            Page page = pageService.getByPaymentId(preferenceId);

            // Gera QR code
            byte[] qrCode = qrCodeService.generate("https://sualoja.com/p/" + page.getSlug());

            emailService.sendEmailWithQRCode(
                    page.getUser().getEmail(),
                    page.getUser().getUsername(),
                    qrCode
            );

            // Atualiza status da página
            pageService.markAsPaid(page.getId());
        }

        return ResponseEntity.ok("OK");
    }

}
