package com.loveapp.love_app_backend.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WheelItem {
    private String id;
    private String texto;
    private String cor;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}
