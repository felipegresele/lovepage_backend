package com.loveapp.love_app_backend.modal.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PixPaymentResponseDTO {
    private Long paymentId;
    private String qrCode;        // string "copia e cola"
    private String qrCodeBase64;  // imagem base64 para exibir
    private String status;
}
