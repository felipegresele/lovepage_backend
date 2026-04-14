package com.loveapp.love_app_backend.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnigmaItem {
    private String id;
    private String texto;
    // `revelado` é estado de UI — não precisa persistir, mas aceita sem erro
    private boolean revelado;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public boolean isRevelado() { return revelado; }
    public void setRevelado(boolean revelado) { this.revelado = revelado; }
}
