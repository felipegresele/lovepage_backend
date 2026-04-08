package com.loveapp.love_app_backend.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PageScheduler {

    private final PageService pageService;

    public PageScheduler(PageService pageService) {
        this.pageService = pageService;
    }

    // Executa a cada hora
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void removeExpiredPages() {
        pageService.deleteExpiredPages();
    }
}
