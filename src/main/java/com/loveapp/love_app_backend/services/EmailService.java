package com.loveapp.love_app_backend.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class EmailService {

    @Value("${resend.api-key}")
    private String resendApiKey;

    public void sendEmailWithQRCode(String to, String userName, byte[] qrCodeBytes) throws ResendException {

        Resend resend = new Resend(resendApiKey);

        String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);

        Attachment attachment = Attachment.builder()
                .fileName("qrcode.png")
                .content(qrCodeBase64)
                .build();

        String body = String.format(
                "Olá %s,\n\n" +
                        "Aqui é da equipe Rearts! Muito obrigado pela sua compra.\n\n" +
                        "Sua página romântica personalizada está pronta para ser acessada.\n" +
                        "Basta escanear o QR Code em anexo para visualizar a página.\n\n" +
                        "Esperamos que você aproveite e compartilhe momentos especiais!\n\n" +
                        "Com carinho,\nEquipe Rearts",
                userName
        );

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Rearts <onboarding@resend.dev>")
                .to(List.of(to))
                .subject("Sua página personalizada Rearts está pronta!")
                .text(body)
                .attachments(List.of(attachment))
                .build();

        CreateEmailResponse response = resend.emails().send(params);
        System.out.println("[EMAIL] Enviado com sucesso! id=" + response.getId());
    }
}