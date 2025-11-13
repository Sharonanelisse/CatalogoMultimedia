package com.smarroquin.catalogomultimedia.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.smarroquin.catalogomultimedia.enums.*;
import java.sql.Timestamp;

@Entity
public class media_titles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long media_titles_id;

    @Column(length = 150)
    @NotNull(message = "El nombre no puede estar en blanco")
    @Size(min=2,max=150)
    private String title_name;

    @NotNull(message = "Seleccionar un campo")
    @Enumerated(EnumType.STRING)
    private title_type title_type;

    @Min(1900)
    @Max(2100)
    private int release_year;

    @Size(max=1000)
    private String synopsis;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private double average_rating;

    @PrePersist
    protected void onCreate() {
        this.created_at = new Timestamp(System.currentTimeMillis());
    }

    private Timestamp created_at;

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("media_titles{");
        sb.append("media_titles_id=").append(media_titles_id);
        sb.append(", media_titles_id='").append(media_titles_id).append('\'');
        sb.append(", title_type='").append(title_type).append('\'');
        sb.append("release_year=").append(release_year).append('\'');
        sb.append(", synopsis='").append(synopsis).append('\'');
        sb.append(", average_rating='").append(average_rating).append('\'');
        sb.append(", created_at=").append(created_at);
        sb.append('}');
        return sb.toString();
    }

}
