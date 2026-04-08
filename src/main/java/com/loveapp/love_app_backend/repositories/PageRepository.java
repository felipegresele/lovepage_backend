package com.loveapp.love_app_backend.repositories;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.types.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID> {

    Optional<Page> findBySlug(String slug);

    List<Page> findByPlanType(PlanType planType);

}