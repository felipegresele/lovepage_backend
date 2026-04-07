package com.loveapp.love_app_backend.controllers;

import com.loveapp.love_app_backend.modal.Page;
import com.loveapp.love_app_backend.modal.dtos.CreatePageDTO;
import com.loveapp.love_app_backend.services.PageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/love-pages")
public class PageController {

    private final PageService service;

    public PageController(PageService service) {
        this.service = service;
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

}
