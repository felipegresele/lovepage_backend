package com.loveapp.love_app_backend.modal;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    private BigDecimal amount;

    private String status;

    private String provider;

    @ManyToOne
    private Page page;

    private LocalDateTime createdAt;

}
