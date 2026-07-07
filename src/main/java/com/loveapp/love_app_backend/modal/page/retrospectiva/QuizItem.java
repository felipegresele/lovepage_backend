package com.loveapp.love_app_backend.modal.page.retrospectiva;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizItem {

    private String id;
    private String pergunta;
    private List<String> opcoes;      // até 4 opções
    private Integer respostaCorreta;  // índice da opção correta (0 a 3)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPergunta() { return pergunta; }
    public void setPergunta(String pergunta) { this.pergunta = pergunta; }

    public List<String> getOpcoes() { return opcoes; }
    public void setOpcoes(List<String> opcoes) { this.opcoes = opcoes; }

    public Integer getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(Integer respostaCorreta) { this.respostaCorreta = respostaCorreta; }
}
