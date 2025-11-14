package com.smarroquin.catalogomultimedia.services;

import com.smarroquin.catalogomultimedia.models.*;
import com.smarroquin.catalogomultimedia.repositories.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class Movie_genresService {
    @Inject
    Movie_genresRepository movieGenresRepo;

    public List<Movie_genres> movie_genres() { return movieGenresRepo.findAll(); }

    public Movie_genres guardar(Movie_genres mg) {return movieGenresRepo.guardar(mg);}

    public void eliminar(Movie_genres mg) {movieGenresRepo.eliminar(mg);}

}
