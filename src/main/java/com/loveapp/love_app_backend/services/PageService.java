package com.loveapp.love_app_backend.services;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.User;
import com.loveapp.love_app_backend.modal.dtos.CreatePageDTO;
import com.loveapp.love_app_backend.modal.types.PlanType;
import com.loveapp.love_app_backend.repositories.PageRepository;
import com.loveapp.love_app_backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PageService {

    private final PageRepository repository;
    private final UserRepository userRepository;

    public PageService(PageRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Page> getAllPages() {
        return repository.findAll();
    }

    public Page createDraft(CreatePageDTO dto){

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        Page page = Page.builder()
                .user(user)
                .receiverName(dto.getReceiverName())
                .senderName(dto.getSenderName())
                .message(dto.getMessage())
                .musicId(dto.getMusicId())
                .musicTitle(dto.getMusicTitle())
                .theme(dto.getTheme())
                .relationshipStartDate(dto.getRelationshipStartDate())
                .photos(dto.getPhotos())
                .slug(UUID.randomUUID().toString().substring(0,6))
                .status("DRAFT")
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(page);
    }

    public void deletePage(UUID pageId) {
        Page page = repository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada!"));
        repository.delete(page);
    }

    public void deleteExpiredPages() {
        // Busca todas as páginas normais
        List<Page> normalPages = repository.findByPlanType(PlanType.NORMAL);

        for (Page page : normalPages) {
            // Verifica se já passou 24 horas
            if (Duration.between(page.getCreatedAt(), LocalDateTime.now()).toHours() >= 24) {
                repository.delete(page);
            }
        }
    }

    public Page getBySlug(String slug){
        return repository.findBySlug(slug).orElseThrow();
    }

}