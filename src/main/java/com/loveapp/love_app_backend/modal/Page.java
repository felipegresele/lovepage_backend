package com.loveapp.love_app_backend.modal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.loveapp.love_app_backend.modal.types.PageStatus;
import com.loveapp.love_app_backend.modal.types.PlanType;
import com.loveapp.love_app_backend.modal.types.QrCodeFrame;
import jakarta.persistence.*;
import lombok.*;
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

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    private PageStatus status = PageStatus.PENDING;

    private LocalDateTime createdAt;

    private String paymentId;

    @Enumerated(EnumType.STRING)
    private QrCodeFrame qrCodeFrame;

    @ElementCollection
    @CollectionTable(name = "page_photos", joinColumns = @JoinColumn(name = "page_id"))
    @Column(name = "photo_url")
    private List<String> photos;

}