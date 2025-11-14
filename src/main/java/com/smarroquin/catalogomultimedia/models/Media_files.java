package com.smarroquin.catalogomultimedia.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.smarroquin.catalogomultimedia.enums.*;
import java.sql.Timestamp;


@Entity
public class Media_files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long media_files_id;

    @NotNull(message = "Debe asociarse a un título")
    @ManyToOne(optional = false)
    @JoinColumn(name = "media_title_id", nullable = false)
    private Media_titles media_titles;

    @NotNull(message = "Seleccionar un campo")
    @Enumerated(EnumType.STRING)
    private file_type file_type;

    @NotNull(message = "Debe haber un URL asociado")
    @Column(length = 500)
    private String blob_url;

    @NotNull(message = "Debe haber un nombre asociado a la URL")
    @Column(length = 500)
    private String blobName;

    @Column(length = 100)
    private String etag;

    @Pattern(regexp = "image/jpeg|image/png|application/pdf", message = "Tipo MIME no válido")
    @Column(length = 50)
    private String content_type;

    @NotNull(message = "Debe especificar el tamaño del archivo")
    private Long size_bytes;

    @PrePersist
    protected void onCreate() {
        this.uploaded_at = new Timestamp(System.currentTimeMillis());
    }

    private Timestamp uploaded_at;

    @Column(length = 50)
    private String uploaded_by;

    public boolean isValidSize() {
        if (content_type == null || size_bytes == null) return false;
        return switch (content_type) {
            case "image/jpeg", "image/png" -> size_bytes <= 2_000_000;
            case "application/pdf" -> size_bytes <= 5_000_000;
            default -> false;
        };
    }

    //Getters and Setters

    public String getBlobName() {
        return blobName;
    }

    public void setBlobName(String blobName) {
        this.blobName = blobName;
    }

    public Long getMedia_files_id() {
        return media_files_id;
    }

    public void setMedia_files_id(Long media_files_id) {
        this.media_files_id = media_files_id;
    }

    public Media_titles getMedia_titles() {
        return media_titles;
    }

    public void setMedia_titles(Media_titles media_titles) {
        this.media_titles = media_titles;
    }

    public file_type getFile_type() {
        return file_type;
    }

    public void setFile_type(file_type file_type) {
        this.file_type = file_type;
    }

    public String getBlob_url() {
        return blob_url;
    }

    public void setBlob_url(String blob_url) {
        this.blob_url = blob_url;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public Long getSize_bytes() {
        return size_bytes;
    }

    public void setSize_bytes(Long size_bytes) {
        this.size_bytes = size_bytes;
    }

    public Timestamp getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(Timestamp uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public String getUploaded_by() {
        return uploaded_by;
    }

    public void setUploaded_by(String uploaded_by) {
        this.uploaded_by = uploaded_by;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("media_titles{");
        sb.append("media_files_id=").append(media_files_id);
        sb.append(", media_titles='").append(media_titles).append('\'');
        sb.append(", file_type='").append(file_type).append('\'');
        sb.append("blob_url=").append(blob_url).append('\'');
        sb.append(", etag='").append(etag).append('\'');
        sb.append(", content_type='").append(content_type).append('\'');
        sb.append(", size_bytes=").append(size_bytes).append('\'');
        sb.append(", uploaded_at=").append(uploaded_at).append('\'');
        sb.append(", uploaded_by=").append(uploaded_by);
        sb.append('}');
        return sb.toString();
    }
}
