package com.loveapp.love_app_backend.modal.dtos;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public class UpdateRetrospectiveDTO {

    private UUID pageId;

    // JsonNode aceita qualquer estrutura JSON sem precisar mapear cada seção
    private JsonNode retrospective;

    public UUID getPageId() {
        return pageId;
    }

    public void setPageId(UUID pageId) {
        this.pageId = pageId;
    }

    public JsonNode getRetrospective() {
        return retrospective;
    }

    public void setRetrospective(JsonNode retrospective) {
        this.retrospective = retrospective;
    }

}
