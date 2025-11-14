package com.smarroquin.catalogomultimedia.repositories;

import com.smarroquin.catalogomultimedia.models.Movie_genres;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Movie_genresRepository extends BaseRepository<Movie_genres, Long> {
    @Override
    protected Class<Movie_genres> entity() { return Movie_genres.class; }
}
