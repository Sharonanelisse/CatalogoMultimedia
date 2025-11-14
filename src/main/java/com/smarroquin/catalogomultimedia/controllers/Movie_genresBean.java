package com.smarroquin.catalogomultimedia.controllers;

import com.smarroquin.catalogomultimedia.models.*;
import com.smarroquin.catalogomultimedia.services.*;

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
public class Movie_genresBean implements Serializable {
    @Inject
    Movie_genresService MGservice;

    @Inject
    private Validator validator;

    private Movie_genres MGselected;
    private boolean dialogVisible;

    @PostConstruct
    public void init() {
        MGselected = new Movie_genres();
        dialogVisible = false;
    }

    public List<Movie_genres> getList(){ return MGservice.movie_genres();}

    public void nuevo() {
        clearFacesMessages();
        MGselected = new Movie_genres();
        dialogVisible = true;
    }

    public void editar(Movie_genres mf) {
        clearFacesMessages();
        this.MGselected = mf;
        dialogVisible = true;
    }

    public void guardar() {
        Set<ConstraintViolation<Movie_genres>> violations = validator.validate(MGselected);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<Movie_genres> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                String label = getFieldLabel(field);

                FacesContext.getCurrentInstance().addMessage("frmMovieGenres:frmMovieGenres",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                label + ": " + message, null));
            }

            FacesContext.getCurrentInstance().validationFailed();
            return;
        }


        MGservice.guardar(MGselected);
        dialogVisible = false;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Genero guardado", "Operación exitosa"));
        MGselected = new Movie_genres();
    }

    public void eliminar(Movie_genres mf) {
        MGservice.eliminar(mf);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Genero eliminado", null));
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
        labels.put("genre_name", "Nombre único del género");
        return labels.getOrDefault(fieldName, fieldName);
    }

    public Movie_genres getSelected() {
        if (MGselected == null) {
            MGselected = new Movie_genres();
        }
        return MGselected;
    }

    public void cancelar() {
        MGselected = new Movie_genres();
    }

    public void setSelected(Movie_genres MGselected) {
        this.MGselected = MGselected;
    }

    public boolean isDialogVisible() {
        return dialogVisible;
    }

    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }

}
