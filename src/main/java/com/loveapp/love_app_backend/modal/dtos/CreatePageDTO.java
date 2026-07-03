package com.loveapp.love_app_backend.modal.dtos;

import com.loveapp.love_app_backend.modal.types.PageStatus;
import com.loveapp.love_app_backend.modal.types.PlanType;
import com.loveapp.love_app_backend.modal.types.TipoPresenteado;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreatePageDTO {

    private UUID userId;

    private String receiverName;

    private String senderName;

    private String message;

    private LocalDate relationshipStartDate;

    private PageStatus status = PageStatus.PENDING;

    private TipoPresenteado tipoPresenteado;

    private String musicId;

    private String musicTitle;

    private String theme;

    private String pageTemplate;

    private List<String> photos;

    private PlanType planType;

}