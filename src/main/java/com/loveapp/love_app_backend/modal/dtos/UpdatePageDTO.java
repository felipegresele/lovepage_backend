package com.loveapp.love_app_backend.modal.dtos;

import com.loveapp.love_app_backend.modal.Retrospectiva;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UpdatePageDTO {

    private String receiverName;

    private String senderName;

    private String message;

    private LocalDate relationshipStartDate;

    private String musicId;

    private String musicTitle;

    private String theme;

    /**
     * Apenas URLs já hospedadas (Cloudinary).
     * Novas fotos devem passar pelo endpoint de upload antes.
     */
    private List<String> photos;

    /**
     * Só é aceito se a página já possuía retrospectiva salva.
     * Validado no Service antes de persistir.
     */
    private Retrospectiva retrospectiva;
}
