package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.User;
import com.loveapp.love_app_backend.modal.dtos.CreatePageDTO;
import com.loveapp.love_app_backend.services.PageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/love-pages")
public class PageController {

    private final PageService service;

    public PageController(PageService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Page>> getAll() {
        List<Page> pages = service.getAllPages();
        return ResponseEntity.ok(pages);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreatePageDTO dto){

        Page page = service.createDraft(dto);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> get(@PathVariable String slug){

        return ResponseEntity.ok(service.getBySlug(slug));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePage(@PathVariable("id") UUID id) {
        service.deletePage(id);
        return ResponseEntity.ok().body("Página excluída com sucesso!");
    }

}
