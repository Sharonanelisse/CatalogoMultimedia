package com.smarroquin.catalogomultimedia.repositories;

import com.smarroquin.catalogomultimedia.models.Media_titles_genres;

public class Media_titles_genresRepository extends BaseRepository<Media_titles_genres, Long> {
    @Override
    protected Class<Media_titles_genres> entity() { return Media_titles_genres.class; }
}