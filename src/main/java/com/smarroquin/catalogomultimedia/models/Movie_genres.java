package com.smarroquin.catalogomultimedia.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
public class Movie_genres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movie_genre_id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(unique = true)
    private String genre_name;

    public Long getMovie_genre_id() {
        return movie_genre_id;
    }

    public void setMovie_genre_id(Long movie_genre_id) {
        this.movie_genre_id = movie_genre_id;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("media_titles{");
        sb.append("movie_genre_id=").append(movie_genre_id);
        sb.append(", genre_name='").append(genre_name);
        sb.append('}');
        return sb.toString();
    }
}
