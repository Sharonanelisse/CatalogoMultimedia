package com.smarroquin.catalogomultimedia.controllers;

import com.smarroquin.catalogomultimedia.dtos.MediaFileDTO;
import com.smarroquin.catalogomultimedia.enums.file_type;
import com.smarroquin.catalogomultimedia.enums.title_type;
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

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Named("mediaTitlesBean")
@ViewScoped
public class Media_titlesBean implements Serializable {

    @Inject
    private media_titlesService MTservice;

    @Inject
    private Movie_genresService MGservice;

    @Inject
    private Media_filesService MFService;

    private Media_titles_genresService MTGService;

    @Inject
    private AzureBlobStorageService azureBlobStorageService;

    @Inject
    private Validator validator;

    private Media_titles MTselected;
    private Media_files selectedFile;
    private boolean dialogVisible;

    private file_type uploadingFileType;


    @PostConstruct
    public void init() {
        MTselected = new Media_titles();
        dialogVisible = false;
    }

    /* --------- Data providers --------- */
    public List<Media_titles> getList() {
        return MTservice.media_titles();
    }

    public List<Movie_genres> getAllGenres() {
        return MGservice.movie_genres();
    }

    public List<Media_files> getFilesOfSelected() {
        if (MTselected == null || MTselected.getMediaFiles() == null) return List.of();
        return MTselected.getMediaFiles().stream()
                .sorted(Comparator.comparing(Media_files::getUploaded_at).reversed())
                .collect(Collectors.toList());
    }

    public title_type[] getTitleTypes() {
        return title_type.values();
    }

    public file_type[] getFileTypes() {
        return file_type.values();
    }

    /* --------- Actions --------- */
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

                FacesContext.getCurrentInstance().addMessage("frmMediaTitle:frmMediaTitle",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                label + ": " + message, null));
            }
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        // Validar que tenga al menos un poster o ficha técnica
        boolean tienePoster = MTselected.getMediaFiles().stream()
                .anyMatch(mf -> mf.getFile_type() == file_type.POSTER);
        boolean tieneFicha = MTselected.getMediaFiles().stream()
                .anyMatch(mf -> mf.getFile_type() == file_type.TECHNICAL_SHEET);

        if (!(tienePoster || tieneFicha)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Debe subir al menos un Poster o una Ficha Técnica", null));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        boolean isNew = MTselected.getMedia_titles_id() == null;
        MTservice.guardar(MTselected);

        FacesMessage msg = new FacesMessage(isNew ?
                "Título agregado con éxito!" : "Título actualizado con éxito!");
        FacesContext.getCurrentInstance().addMessage(null, msg);

        PrimeFaces.current().executeScript("PF('manageMediaTitlesDialog').hide()");
        PrimeFaces.current().ajax().update("frmMediaTitle:messages", "frmMediaTitle:dtMediaTitles");

        MTselected = new Media_titles();
        dialogVisible = false;
    }


    public void eliminar(Media_titles mt) {
        MTservice.eliminar(mt);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Título eliminado", null));
        PrimeFaces.current().ajax().update("frmMediaTitle:messages", "frmMediaTitle:dtMediaTitles");
    }

    /* --------- File upload / delete --------- */
    public void handleFileUpload(FileUploadEvent event) {
        // Validar que el tipo de archivo (enum) esté seleccionado
        if (uploadingFileType == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Debe seleccionar un tipo de archivo antes de subir", null));
            return;
        }

        UploadedFile uf = event.getFile();
        if (uf == null || uf.getSize() == 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se recibió ningún archivo", null));
            return;
        }

        String ct = uf.getContentType();
        long size = uf.getSize();
        boolean isImage = "image/jpeg".equalsIgnoreCase(ct) || "image/png".equalsIgnoreCase(ct);
        boolean isPdf = "application/pdf".equalsIgnoreCase(ct);

        // Validar tipo permitido
        if (!(isImage || isPdf)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Tipo no permitido", ct));
            return;
        }

        // Validar tamaños
        if (isImage && size > 2L * 1024 * 1024) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "La imagen supera 2 MB", uf.getFileName()));
            return;
        }
        if (isPdf && size > 5L * 1024 * 1024) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "El PDF supera 5 MB", uf.getFileName()));
            return;
        }

        try (InputStream in = uf.getInputStream()) {
            // Subir al blob storage
            MediaFileDTO dto = azureBlobStorageService.uploadCatalogFile(
                    uploadingFileType,
                    MTselected.getTitle_name(),
                    uf.getFileName(),
                    ct,
                    in,
                    size,
                    "ui",
                    Duration.ofMinutes(30),
                    true // openInline
            );

            // Construir entidad Media_files con datos del DTO
            Media_files mf = new Media_files();
            mf.setMedia_titles(MTselected);
            mf.setFile_type(dto.getFile_type());
            mf.setBlob_url(dto.getBlob_url());
            mf.setBlobName(dto.getBlobName());
            mf.setEtag(dto.getEtag());
            mf.setContent_type(dto.getContent_type());
            mf.setSize_bytes(dto.getSize_bytes());
            mf.setUploaded_by(dto.getUploaded_by());

            if (dto.getUploaded_at() != null) {
                mf.setUploaded_at(java.sql.Timestamp.valueOf(dto.getUploaded_at().toLocalDateTime()));
            }

            // Asociar al título seleccionado
            MTselected.getMediaFiles().add(mf);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Archivo cargado: " + uf.getFileName(), null));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al subir archivo", ex.getMessage()));
        }

        PrimeFaces.current().ajax().update("frmMediaTitle:mediaFilesTable", "frmMediaTitle:messages");
    }


    public void deleteMediaFile() {
        if (selectedFile == null) return;

        if (selectedFile.getMedia_files_id() != null) {
            MFService.eliminar(selectedFile);
        }

        Set<Media_files> set = MTselected.getMediaFiles();
        if (set != null) set.removeIf(mf -> mf.getBlob_url().equals(selectedFile.getBlob_url()));

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Archivo eliminado"));
        PrimeFaces.current().ajax().update("frmMediaTitle:mediaFilesTable", "frmMediaTitle:messages");
    }

    /* --------- Helpers --------- */
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
        labels.put("title_name", "Nombre de la película o serie");
        labels.put("title_type", "Tipo de producción");
        labels.put("release_year", "Año de lanzamiento");
        labels.put("synopsis", "Sinopsis");
        labels.put("average_rating", "Calificación promedio");
        labels.put("created_at", "Fecha de creación");

        return labels.getOrDefault(fieldName, fieldName);
    }

    public String joinGenres(Media_titles_genres mt) {
        if (mt == null || mt.getMovie_genres() == null) return "";
        return mt.getMovie_genres().getGenre_name();
    }


    /* --------- Getters/Setters --------- */
    public Media_titles getSelected() {
        if (MTselected == null) {
            MTselected = new Media_titles();
        }
        return MTselected;
    }

    public void setSelected(Media_titles selected) {
        this.MTselected = selected;
    }

    public Media_files getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(Media_files selectedFile) {
        this.selectedFile = selectedFile;
    }

    public boolean isDialogVisible() {
        return dialogVisible;
    }

    public void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
    }

    public file_type getUploadingFileType() {
        return uploadingFileType;
    }

    public void setUploadingFileType(file_type uploadingFileType) {
        this.uploadingFileType = uploadingFileType;
    }

}

