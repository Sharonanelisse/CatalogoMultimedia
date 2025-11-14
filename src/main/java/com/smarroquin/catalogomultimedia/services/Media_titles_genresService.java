package com.smarroquin.catalogomultimedia.services;

import com.smarroquin.catalogomultimedia.models.Media_titles;
import com.smarroquin.catalogomultimedia.models.Media_titles_genres;
import com.smarroquin.catalogomultimedia.models.Movie_genres;
import com.smarroquin.catalogomultimedia.repositories.Media_titlesRepository;
import com.smarroquin.catalogomultimedia.repositories.Media_titles_genresRepository;
import com.smarroquin.catalogomultimedia.repositories.Movie_genresRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class Media_titles_genresService {

    @Inject
    private Media_titles_genresRepository mediaTitleGenresRepo;

    @Inject
    private Media_titlesRepository mediaTitleRepo;

    @Inject
    private Movie_genresRepository movieGenreRepo;


    public List<Media_titles_genres> media_titles_genres() {
        return mediaTitleGenresRepo.findAll();
    }


    public Media_titles_genres addGenreToTitle(Long media_title_id, Long movie_genre_id) {

        Media_titles title = mediaTitleRepo.find(media_title_id);
        Movie_genres genre = movieGenreRepo.find(movie_genre_id);

        if (title == null) throw new IllegalArgumentException("Título no existe.");
        if (genre == null) throw new IllegalArgumentException("Género no existe.");

        // Validar regla del negocio: combinación única
        Media_titles_genres existing = mediaTitleGenresRepo.findByTitleIdAndGenreId(media_title_id, movie_genre_id);

        if (existing != null) {
            throw new IllegalStateException("Esta combinación ya existe.");
        }

        Media_titles_genres relation = new Media_titles_genres();
        relation.setMedia_titles(title);
        relation.setMovie_genres(genre);

        return mediaTitleGenresRepo.guardar(relation);
    }


    public void removeGenreFromTitle(Long media_title_id, Long movie_genre_id) {

        Media_titles_genres existing =
                mediaTitleGenresRepo.findByTitleIdAndGenreId(media_title_id, movie_genre_id);

        if (existing != null) {
            mediaTitleGenresRepo.eliminar(existing);
        }
    }

}
