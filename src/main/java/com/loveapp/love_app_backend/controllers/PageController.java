package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.User;
import com.loveapp.love_app_backend.modal.dtos.CreatePageDTO;
import com.loveapp.love_app_backend.services.EmailService;
import com.loveapp.love_app_backend.services.PageService;
import com.loveapp.love_app_backend.services.QRCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/love-pages")
public class PageController {

    private final PageService service;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    public PageController(PageService service,QRCodeService qrCodeService ,EmailService emailService) {
        this.service = service;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<Page>> getAll() {
        List<Page> pages = service.getAllPages();
        return ResponseEntity.ok(pages);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreatePageDTO dto){

        Page page = service.createDraft(dto);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> get(@PathVariable String slug){

        return ResponseEntity.ok(service.getBySlug(slug));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePage(@PathVariable("id") UUID id) {
        service.deletePage(id);
        return ResponseEntity.ok().body("Página excluída com sucesso!");
    }

    @PostMapping("/{id}/send-qr")
    public ResponseEntity<?> sendQrCodeEmail(@PathVariable UUID id) {
        try {
            Page page = service.getById(id); // você precisa criar esse método no PageService
            byte[] qrCode = qrCodeService.generate("https://heartlink-ten.vercel.app/p/" + page.getSlug());
            emailService.sendEmailWithQRCode(
                    page.getUser().getEmail(),
                    page.getUser().getUsername(),
                    qrCode
            );
            return ResponseEntity.ok("Email com QR code enviado para: " + page.getUser().getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }

}
