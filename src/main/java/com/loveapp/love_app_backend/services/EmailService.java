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

        String subject = String.format("Sua surpresa da HeartCode está pronta, %s!", userName);

        String body = String.format(
                "Olá, %s!\n\n" +

                        "O amor está nos detalhes… e agora ele também está em um código exclusivo ❤️\n\n" +

                        "Sua página personalizada da HeartCode já saiu do forno e está pronta para emocionar!\n" +
                        "O QR Code em anexo é a chave para acessar cada foto, palavra e detalhe que você preparou.\n\n" +

                        "Ficamos muito felizes em ajudar você a transformar sentimentos em uma experiência digital única.\n\n" +

                        "💡 Ideias para surpreender:\n" +
                        "• Mensagem inesperada: Envie o QR Code por WhatsApp no meio do dia com um \"pensei em você\".\n" +
                        "• Presente físico: Imprima o código e coloque dentro de uma caixa de bombons ou um envelope bonito.\n" +
                        "• Surpresa no celular: Use o QR Code como papel de parede por alguns minutos e deixe a pessoa descobrir.\n\n" +

                        "Com carinho,\n" +
                        "Equipe HeartCode",
                userName
        );

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("HeartCode <onboarding@resend.dev>")
                .to(List.of(to))
                .subject(subject)
                .text(body)
                .attachments(List.of(attachment))
                .build();

        CreateEmailResponse response = resend.emails().send(params);
    }
}