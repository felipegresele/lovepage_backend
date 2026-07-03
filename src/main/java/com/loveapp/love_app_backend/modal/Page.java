package com.loveapp.love_app_backend.modal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.loveapp.love_app_backend.modal.types.PageStatus;
import com.loveapp.love_app_backend.modal.types.PlanType;
import com.loveapp.love_app_backend.modal.types.QrCodeFrame;
import com.loveapp.love_app_backend.modal.types.TipoPresenteado;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "love_pages")
public class Page {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Column(unique = true)
    private String slug;

    private String receiverName;

    private String senderName;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDate relationshipStartDate;

    private String musicId;

    private String musicTitle;

    private String theme;

    /**
     * Define qual template visual é usado para renderizar a página pública
     * (ex: "PADRAO", "SPOTIFY"). Não confundir com "theme", que controla o
     * modo de exibição (padrao/classico/simples) dentro do template padrão.
     */
    @Builder.Default
    private String pageTemplate = "PADRAO";

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    private PageStatus status = PageStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_presenteado")
    private TipoPresenteado tipoPresenteado = TipoPresenteado.CASAL;

    private LocalDateTime createdAt;

    private String paymentId;

    @Enumerated(EnumType.STRING)
    private QrCodeFrame qrCodeFrame;

    @ElementCollection
    @CollectionTable(name = "page_photos", joinColumns = @JoinColumn(name = "page_id"))
    @Column(name = "photo_url")
    private List<String> photos;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Retrospectiva retrospectiva;

}