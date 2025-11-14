package com.smarroquin.catalogomultimedia.repositories;

import com.smarroquin.catalogomultimedia.models.Media_titles;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class mediaTitlesRepository extends BaseRepository<Media_titles, Long> {
    @Override
    protected Class<Media_titles> entity() { return Media_titles.class; }
}
