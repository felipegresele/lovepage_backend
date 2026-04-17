package com.loveapp.love_app_backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.Retrospectiva;
import com.loveapp.love_app_backend.modal.User;
import com.loveapp.love_app_backend.modal.dtos.CreatePageDTO;
import com.loveapp.love_app_backend.modal.dtos.UpdatePageDTO;
import com.loveapp.love_app_backend.modal.types.PageStatus;
import com.loveapp.love_app_backend.modal.types.PlanType;
import com.loveapp.love_app_backend.modal.types.QrCodeFrame;
import com.loveapp.love_app_backend.repositories.PageRepository;
import com.loveapp.love_app_backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
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

    // ── Métodos existentes ────────────────────────────────────────────────────

    public List<Page> getAllPages() {
        return repository.findAll();
    }

    public Page createDraft(CreatePageDTO dto) {
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
                .planType(planType)
                .slug(UUID.randomUUID().toString().substring(0, 6))
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
        List<Page> normalPages = repository.findByPlanType(PlanType.PADRAO);
        for (Page page : normalPages) {
            if (Duration.between(page.getCreatedAt(), LocalDateTime.now()).toHours() >= 24) {
                repository.delete(page);
            }
        }
    }

    public void savePaymentId(UUID pageId, String preferenceId) {
        Page page = repository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada!"));
        page.setStatus(PageStatus.PENDING);
        page.setPaymentId(preferenceId);
        repository.save(page);
    }

    public Page getByPaymentId(String preferenceId) {
        return repository.findByPaymentId(preferenceId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada pelo pagamento!"));
    }

    public void markAsPaid(UUID pageId) {
        Page page = repository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada!"));
        page.setStatus(PageStatus.PAID);
        repository.save(page);
    }

    public Page getBySlug(String slug) {
        return repository.findBySlug(slug).orElseThrow();
    }

    public Page getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Página não encontrada!"));
    }

    public void saveQrCodeFrame(UUID pageId, QrCodeFrame qrCodeFrame) {
        Page page = repository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada: " + pageId));
        page.setQrCodeFrame(qrCodeFrame);
        repository.save(page);
    }

    public void salvarRetrospectiva(UUID pageId, Retrospectiva retrospectiva) {
        Page page = repository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada: " + pageId));
        page.setRetrospectiva(retrospectiva);
        repository.save(page);
    }

    // ── NOVOS métodos ─────────────────────────────────────────────────────────

    /**
     * Retorna todas as páginas de um usuário, inclusive sem páginas (lista vazia).
     */
    public List<Page> getPagesByUserId(UUID userId) {
        // Valida se o usuário existe antes de buscar
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));

        return repository.findByUserId(userId);
    }

    /**
     * Atualiza apenas os campos não-nulos do DTO.
     *
     * REGRA DE OURO: se um campo era nulo na entidade original (ex: retrospectiva),
     * o DTO NÃO pode introduzi-lo. A tentativa resulta em IllegalArgumentException
     * que o Controller converte em HTTP 400.
     */
    @Transactional
    public Page updatePage(UUID pageId, UpdatePageDTO dto) {
        Page page = repository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Página não encontrada: " + pageId));

        // ── Campos simples: só sobrescreve se o DTO trouxer valor ──────────────
        if (dto.getReceiverName() != null) {
            page.setReceiverName(dto.getReceiverName());
        }
        if (dto.getSenderName() != null) {
            page.setSenderName(dto.getSenderName());
        }
        if (dto.getMessage() != null) {
            page.setMessage(dto.getMessage());
        }
        if (dto.getRelationshipStartDate() != null) {
            page.setRelationshipStartDate(dto.getRelationshipStartDate());
        }
        if (dto.getMusicId() != null) {
            page.setMusicId(dto.getMusicId());
        }
        if (dto.getMusicTitle() != null) {
            page.setMusicTitle(dto.getMusicTitle());
        }
        if (dto.getTheme() != null) {
            page.setTheme(dto.getTheme());
        }
        if (dto.getPhotos() != null) {
            page.setPhotos(dto.getPhotos());
        }

        // ── Retrospectiva: BLOQUEADA se não existia na criação ─────────────────
        if (dto.getRetrospectiva() != null) {
            if (page.getRetrospectiva() == null) {
                throw new IllegalArgumentException(
                        "Não é permitido adicionar retrospectiva durante a edição. " +
                                "Essa funcionalidade não foi incluída na criação desta página."
                );
            }
            // Existe no original → atualiza apenas as seções que existiam
            page.setRetrospectiva(
                    mergeRetrospectiva(page.getRetrospectiva(), dto.getRetrospectiva())
            );
        }

        return repository.save(page);
    }

    /**
     * Merge seguro de Retrospectiva: só atualiza sub-seções que já existiam
     * na entidade original. Garante que o usuário não adiciona seções novas.
     */
    private Retrospectiva mergeRetrospectiva(Retrospectiva original, Retrospectiva incoming) {
        // selectedSections define o contrato do que o usuário criou
        if (incoming.getTimeline() != null && original.getTimeline() != null) {
            original.setTimeline(incoming.getTimeline());
        }
        if (incoming.getWheel() != null && original.getWheel() != null) {
            original.setWheel(incoming.getWheel());
        }
        if (incoming.getGallery() != null && original.getGallery() != null) {
            original.setGallery(incoming.getGallery());
        }
        if (incoming.getEnigma() != null && original.getEnigma() != null) {
            original.setEnigma(incoming.getEnigma());
        }
        // efeitoTime é sempre editável se retrospectiva existe
        original.setEfeitoTime(incoming.isEfeitoTime());

        // ── Campos opcionais de memórias: sempre editáveis (podem ser nulos) ──
        if (incoming.getOndeSeConheceram() != null) {
            original.setOndeSeConheceram(incoming.getOndeSeConheceram());
        }
        if (incoming.getMomentoFavorito() != null) {
            original.setMomentoFavorito(incoming.getMomentoFavorito());
        }
        if (incoming.getProximoPasso() != null) {
            original.setProximoPasso(incoming.getProximoPasso());
        }

        return original;
    }
}