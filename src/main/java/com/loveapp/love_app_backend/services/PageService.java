package com.loveapp.love_app_backend.services;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.dtos.CreatePageDTO;
import com.loveapp.love_app_backend.repositories.PageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PageService {

    private final PageRepository repository;

    public PageService(PageRepository repository) {
        this.repository = repository;
    }

    public Page createDraft(CreatePageDTO dto){

        Page page = Page.builder()
                .receiverName(dto.getReceiverName())
                .senderName(dto.getSenderName())
                .message(dto.getMessage())
                .musicUrl(dto.getMusicUrl())
                .theme(dto.getTheme())
                .relationshipStartDate(dto.getRelationshipStartDate())
                .photos(dto.getPhotos())
                .slug(UUID.randomUUID().toString().substring(0,6))
                .status("DRAFT")
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(page);
    }

    public Page getBySlug(String slug){
        return repository.findBySlug(slug).orElseThrow();
    }

}