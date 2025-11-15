package com.smarroquin.catalogomultimedia.controllers;

import com.smarroquin.catalogomultimedia.models.Media_files;
import com.smarroquin.catalogomultimedia.services.Media_filesService;
import com.smarroquin.catalogomultimedia.enums.file_type;

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
public class Media_filesBean implements Serializable {

    @Inject
    Media_filesService service;

    @Inject
    private Validator validator;

    private Media_files selected;
    private boolean dialogVisible;

    @PostConstruct
    public void init() {
        selected = new Media_files();
        dialogVisible = false;
    }

    public List<Media_files> getList(){ return service.media_files();}

    public file_type[] getFileTypes() {
        return file_type.values();
    }

    public void nuevo() {
        clearFacesMessages();
        selected = new Media_files();
        dialogVisible = true;
    }

    public void editar(Media_files mf) {
        clearFacesMessages();
        this.selected = mf;
        dialogVisible = true;
    }

    public void guardar() {
        Set<ConstraintViolation<Media_files>> violations = validator.validate(selected);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<Media_files> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                String label = getFieldLabel(field);

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                label + ": " + message, null));
            }

            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        service.guardar(selected);
        dialogVisible = false;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo guardado", "Operación exitosa"));
        selected = new Media_files();
    }

    public void eliminar(Media_files mf) {
        service.eliminar(mf);
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
        labels.put("media_title_id", "Título al que pertenece el archivo.");
        labels.put("file_type", "Tipo de archivo almacenado.");
        labels.put("url", "URL del archivo en Azure Blob Storage.");
        labels.put("etag", "Identificador de versión del blob");
        labels.put("content_type", "Tipo de contenido del archivo");
        labels.put("size_bytes", "Tamaño del archivo");
        labels.put("uploaded_at", "Fecha de subida del archivo");
        labels.put("uploaded_by", "Usuario que subio eßl archivo");

        return labels.getOrDefault(fieldName, fieldName);
    }

    public Media_files getSelected() {
        if (selected == null) {
            selected = new Media_files();
        }
        return selected;
    }

    public void cancelar() {
        selected = new Media_files();
    }

    public void setSelected(Media_files selected) {
        this.selected = selected;
    }

    public boolean isDialogVisible() {
        return dialogVisible;
    }

    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }

}
