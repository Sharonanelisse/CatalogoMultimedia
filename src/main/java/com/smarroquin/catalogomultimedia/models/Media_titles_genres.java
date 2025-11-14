package com.smarroquin.catalogomultimedia.models;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"media_title_id", "movie_genre_id"})
        }
)
public class Media_titles_genres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long media_titles_genres_id;

    @NotNull(message = "Debe asociarse a un título")
    @ManyToOne(optional = false)
    @JoinColumn(name = "media_title_id", nullable = false)
    private Media_titles media_titles;

    @NotNull(message = "Debe asociarse a un título")
    @ManyToOne(optional = false)
    @JoinColumn(name = "movie_genre_id", nullable = false)
    private Movie_genres movie_genres;

    public Long getMedia_titles_genres_id() {
        return media_titles_genres_id;
    }

    public void setMedia_titles_genres_id(Long media_titles_genres_id) {
        this.media_titles_genres_id = media_titles_genres_id;
    }

    public Media_titles getMedia_titles() {
        return media_titles;
    }

    public void setMedia_titles(Media_titles media_titles) {
        this.media_titles = media_titles;
    }

    public Movie_genres getMovie_genres() {
        return movie_genres;
    }

    public void setMovie_genres(Movie_genres movie_genres) {
        this.movie_genres = movie_genres;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("media_titles_genres{");
        sb.append("media_titles_genres_id=").append(media_titles_genres_id);
        sb.append(", media_titles='").append(media_titles).append('\'');
        sb.append(", movie_genres='").append(movie_genres);
        sb.append('}');
        return sb.toString();
    }
}
