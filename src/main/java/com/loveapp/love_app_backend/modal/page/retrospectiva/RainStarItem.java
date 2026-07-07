package com.loveapp.love_app_backend.modal.page.retrospectiva;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RainStarItem {

    private String id;

    private String message;

    private Boolean unlocked = false;

}
