package com.loveapp.love_app_backend.services;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.User;
import com.loveapp.love_app_backend.modal.dtos.CreatePageDTO;
import com.loveapp.love_app_backend.modal.types.PageStatus;
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

        PlanType planType = dto.getPlanType();

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
                .planType(planType) // salvar o plano selecionado
                .slug(UUID.randomUUID().toString().substring(0,6))
                .status(PageStatus.PENDING)
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
        List<Page> normalPages = repository.findByPlanType(PlanType.PADRAO);

        for (Page page : normalPages) {
            // Verifica se já passou 24 horas
            if (Duration.between(page.getCreatedAt(), LocalDateTime.now()).toHours() >= 24) {
                repository.delete(page);
            }
        }
    }

    // Salva o preferenceId (checkout) na página
    public void savePaymentId(UUID pageId, String preferenceId) {
        Page page = repository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada!"));
        page.setStatus(PageStatus.PENDING); // status enquanto pagamento não confirmado
        page.setPaymentId(preferenceId); // você precisa criar este campo no Page
        repository.save(page);
    }

    // Busca página pelo preferenceId
    public Page getByPaymentId(String preferenceId) {
        return repository.findByPaymentId(preferenceId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada pelo pagamento!"));
    }

    // Marca a página como paga
    public void markAsPaid(UUID pageId) {
        Page page = repository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada!"));
        page.setStatus(PageStatus.PAID);
        repository.save(page);
    }

    public Page getBySlug(String slug){
        return repository.findBySlug(slug).orElseThrow();
    }

    public Page getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Página não encontrada!"));
    }

}