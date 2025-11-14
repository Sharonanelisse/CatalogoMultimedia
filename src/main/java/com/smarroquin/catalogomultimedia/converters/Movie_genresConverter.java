package com.smarroquin.catalogomultimedia.converters;

import com.smarroquin.catalogomultimedia.models.Movie_genres;
import com.smarroquin.catalogomultimedia.repositories.Movie_genresRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;


@FacesConverter(value = "movie_genresConverter", managed = true)
@ApplicationScoped
public class Movie_genresConverter implements Converter<Movie_genres> {
    @Inject
    Movie_genresRepository repo;

    @Override
    public Movie_genres getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        try{
            Long id = Long.valueOf(value);
            return repo.find(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Movie_genres value) {
        return (value == null || value.getMovie_genre_id() ==null) ? "" : value.getMovie_genre_id().toString();
    }
}
