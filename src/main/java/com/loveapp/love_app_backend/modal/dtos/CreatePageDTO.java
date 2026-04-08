package com.loveapp.love_app_backend.modal.dtos;

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

    private String musicId;

    private String musicTitle;

    private String theme;

    private List<String> photos;

}