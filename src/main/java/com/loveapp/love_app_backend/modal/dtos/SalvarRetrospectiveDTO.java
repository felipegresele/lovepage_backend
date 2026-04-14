package com.loveapp.love_app_backend.modal.dtos;

import com.loveapp.love_app_backend.modal.Retrospectiva;

import java.util.UUID;

public class SalvarRetrospectiveDTO {

    private UUID pageId;
    private Retrospectiva retrospectiva;

    public UUID getPageId() { return pageId; }
    public void setPageId(UUID pageId) { this.pageId = pageId; }

    public Retrospectiva getRetrospectiva() { return retrospectiva; }
    public void setRetrospectiva(Retrospectiva retrospectiva) { this.retrospectiva = retrospectiva; }
}
