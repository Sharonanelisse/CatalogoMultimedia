package com.smarroquin.catalogomultimedia.controllers;

import com.smarroquin.catalogomultimedia.models.Media_titles;
import com.smarroquin.catalogomultimedia.services.mediaTitlesService;

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
public class mediaTitlesBean implements Serializable {

    @Inject
    mediaTitlesService MTservice;

    @Inject
    private Validator validator;

    private Media_titles MTselected;
    private boolean dialogVisible;

    @PostConstruct
    public void init() {
        MTselected = new Media_titles();
        dialogVisible = false;
    }

    public List<Media_titles> getList(){ return MTservice.media_titles();}

    public void nuevo() {
        clearFacesMessages();
        MTselected = new Media_titles();
        dialogVisible = true;
    }

    public void editar(Media_titles mt) {
        clearFacesMessages();
        this.MTselected = mt;
        dialogVisible = true;
    }

    public void guardar() {
        Set<ConstraintViolation<Media_titles>> violations = validator.validate(MTselected);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<Media_titles> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                String label = getFieldLabel(field);

                FacesContext.getCurrentInstance().addMessage("frmMediaFile:frmMediaFile",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                label + ": " + message, null));
            }

            FacesContext.getCurrentInstance().validationFailed();
            return;
        }


        MTservice.guardar(MTselected);
        dialogVisible = false;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo guardado", "Operación exitosa"));
        MTselected = new Media_titles();
    }

    public void eliminar(Media_titles mt) {
        MTservice.eliminar(mt);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo eliminado", null));
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
        labels.put("title_name", "Título de película o serie");
        labels.put("title_type", "Tipo de entretenimiento");
        labels.put("release_year", "Año de publicación");
        labels.put("synopsis", "Sinopsis");
        labels.put("average_rating", "Calificacion promedio");
        labels.put("created_at", "Fecha de creacion");

        return labels.getOrDefault(fieldName, fieldName);
    }

    public Media_titles getSelected() {
        if (MTselected == null) {
            MTselected = new Media_titles();
        }
        return MTselected;
    }

    public void cancelar() {
        MTselected = new Media_titles();
    }

    public void setSelected(Media_titles selected) {
        this.MTselected = selected;
    }

    public boolean isDialogVisible() {
        return dialogVisible;
    }

    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }
    
    
    
}
