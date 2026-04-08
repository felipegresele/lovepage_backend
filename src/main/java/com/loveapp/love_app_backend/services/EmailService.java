package com.loveapp.love_app_backend.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithQRCode(String to, String userName, byte[] qrCodeBytes) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Sua página personalizada Rearts está pronta!");

        String body = String.format(
                "Olá %s,\n\n" +
                        "Aqui é da equipe Rearts! Muito obrigado pela sua compra.\n\n" +
                        "Sua página romântica personalizada está pronta para ser acessada.\n" +
                        "Basta escanear o QR Code em anexo para visualizar a página.\n\n" +
                        "Esperamos que você aproveite e compartilhe momentos especiais!\n\n" +
                        "Com carinho,\nEquipe Rearts",
                userName
        );

        helper.setText(body);

        // Anexando QR code
        ByteArrayResource resource = new ByteArrayResource(qrCodeBytes);
        helper.addAttachment("qrcode.png", resource);

        mailSender.send(message);
    }
}