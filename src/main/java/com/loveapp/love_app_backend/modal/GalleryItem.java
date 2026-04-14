package com.loveapp.love_app_backend.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GalleryItem {

    private String id;
    private String imagem;
    private String descricao;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
