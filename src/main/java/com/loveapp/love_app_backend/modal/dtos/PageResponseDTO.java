package com.loveapp.love_app_backend.modal.dtos;

import lombok.*;

@Getter
@Setter
@Builder
public class PageResponseDTO {

    private String slug;

    private String receiverName;

    private String senderName;

    private String message;

    private String musicUrl;

}