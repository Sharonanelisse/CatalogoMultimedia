package com.smarroquin.catalogomultimedia.controllers;

import com.smarroquin.catalogomultimedia.models.Media_titles_genres;
import com.smarroquin.catalogomultimedia.services.Media_titles_genresService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class Media_titles_genresBean implements Serializable {

    @Inject
    Media_titles_genresService MTGservice;

    @Inject
    private Validator validator;

    private Media_titles_genres MTGselected;
    private boolean dialogVisible;

    @PostConstruct
    public void init() {
        MTGselected = new Media_titles_genres();
        dialogVisible = false;
    }

    public List<Media_titles_genres> getList(){ return MTGservice.media_titles_genres();}

    public void nuevo() {
        clearFacesMessages();
        MTGselected = new Media_titles_genres();
        dialogVisible = true;
    }

    public void editar(Media_titles_genres mtg) {
        clearFacesMessages();
        this.MTGselected = mtg;
        dialogVisible = true;
    }

    public void guardar() {
        try {

            Long titleId = MTGselected.getMedia_titles().getMedia_titles_id();
            Long genreId = MTGselected.getMovie_genres().getMovie_genre_id();

            MTGservice.addGenreToTitle(titleId, genreId);

            dialogVisible = false;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Relación guardada correctamente", null));

            MTGselected = new Media_titles_genres();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            e.getMessage(), null));
        }
    }


    public void eliminar(Media_titles_genres mtg) {

        Long titleId = mtg.getMedia_titles().getMedia_titles_id();
        Long genreId = mtg.getMovie_genres().getMovie_genre_id();

        MTGservice.removeGenreFromTitle(titleId, genreId);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Relación eliminada", null));
    }


    private void clearFacesMessages() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null) return;
        for (Iterator<FacesMessage> it = ctx.getMessages(); it.hasNext(); ) {
            it.next();
            it.remove();
        }
    }

    private String getFieldLabel(String fieldName) {
        Map<String, String> labels = new HashMap<>();
        labels.put("media_titles", "Media Titles");
        labels.put("movie_genres", "Movie Genres");
        return labels.getOrDefault(fieldName, fieldName);
    }

    public Media_titles_genres getSelected() {
        if (MTGselected == null) {
            MTGselected = new Media_titles_genres();
        }
        return MTGselected;
    }

    public void cancelar() {
        MTGselected = new Media_titles_genres();
    }

    public void setSelected(Media_titles_genres selected) {
        this.MTGselected = selected;
    }

    public boolean isDialogVisible() {
        return dialogVisible;
    }

    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }

}
