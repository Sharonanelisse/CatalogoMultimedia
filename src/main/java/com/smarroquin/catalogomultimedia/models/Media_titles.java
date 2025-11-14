package com.smarroquin.catalogomultimedia.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.smarroquin.catalogomultimedia.enums.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Media_titles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long media_titles_id;

    @NotBlank(message = "Debe ingresar un nombre")
    @Column(length = 200, nullable = false)
    private String title_name;

    @NotNull(message = "Debe seleccionar un tipo de producción")
    @Enumerated(EnumType.STRING)
    private title_type title_type;

    private int release_year;

    @Column(length = 1000)
    private String synopsis;

    private double average_rating;

    @Column(nullable = false, updatable = false)
    private Timestamp created_at;

    @PrePersist
    protected void onCreate() {
        this.created_at = new Timestamp(System.currentTimeMillis());
    }

    /* --------- Relación con archivos --------- */
    @OneToMany(mappedBy = "media_titles", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Media_files> mediaFiles = new HashSet<>();

    /* --------- Getters y Setters --------- */

    public Long getMedia_titles_id() {
        return media_titles_id;
    }

    public void setMedia_titles_id(Long media_titles_id) {
        this.media_titles_id = media_titles_id;
    }

    public String getTitle_name() {
        return title_name;
    }

    public void setTitle_name(String title_name) {
        this.title_name = title_name;
    }

    public title_type getTitle_type() {
        return title_type;
    }

    public void setTitle_type(title_type title_type) {
        this.title_type = title_type;
    }

    public int getRelease_year() {
        return release_year;
    }

    public void setRelease_year(int release_year) {
        this.release_year = release_year;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double average_rating) {
        this.average_rating = average_rating;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Set<Media_files> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(Set<Media_files> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Media_titles{");
        sb.append("media_titles_id=").append(media_titles_id);
        sb.append(", title_name='").append(title_name).append('\'');
        sb.append(", title_type=").append(title_type);
        sb.append(", release_year=").append(release_year);
        sb.append(", synopsis='").append(synopsis).append('\'');
        sb.append(", average_rating=").append(average_rating);
        sb.append(", created_at=").append(created_at);
        sb.append('}');
        return sb.toString();
    }
}

