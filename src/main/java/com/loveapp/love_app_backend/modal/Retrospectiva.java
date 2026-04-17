package com.loveapp.love_app_backend.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Retrospectiva {
    private List<String> selectedSections;

    private List<TimelineItem> timeline;
    private List<WheelItem>    wheel;
    private List<GalleryItem>  gallery;
    private List<EnigmaItem>   enigma;
    private Boolean efeitoTime = false;

    private String ondeSeConheceram;
    private String momentoFavorito;
    private String proximoPasso;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public List<String> getSelectedSections() { return selectedSections; }
    public void setSelectedSections(List<String> selectedSections) { this.selectedSections = selectedSections; }

    public List<TimelineItem> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineItem> timeline) { this.timeline = timeline; }

    public List<WheelItem> getWheel() { return wheel; }
    public void setWheel(List<WheelItem> wheel) { this.wheel = wheel; }

    public List<GalleryItem> getGallery() { return gallery; }
    public void setGallery(List<GalleryItem> gallery) { this.gallery = gallery; }

    public List<EnigmaItem> getEnigma() { return enigma; }
    public void setEnigma(List<EnigmaItem> enigma) { this.enigma = enigma; }

    public boolean isEfeitoTime() {return efeitoTime;}
    public void setEfeitoTime(boolean efeitoTime) {
        this.efeitoTime = efeitoTime;
    }

    public String getOndeSeConheceram() { return ondeSeConheceram; }
    public void setOndeSeConheceram(String ondeSeConheceram) { this.ondeSeConheceram = ondeSeConheceram; }

    public String getMomentoFavorito() { return momentoFavorito; }
    public void setMomentoFavorito(String momentoFavorito) { this.momentoFavorito = momentoFavorito; }

    public String getProximoPasso() { return proximoPasso; }
    public void setProximoPasso(String proximoPasso) { this.proximoPasso = proximoPasso; }

}
