package com.loveapp.love_app_backend.modal.dtos;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class PageResponseDTO {

    private String slug;

    private String receiverName;

    private String senderName;

    private String message;

    private String musicId;

    private String musicTitle;

    private List<String> photos;

    private LocalDate relationshipStartDate;

}