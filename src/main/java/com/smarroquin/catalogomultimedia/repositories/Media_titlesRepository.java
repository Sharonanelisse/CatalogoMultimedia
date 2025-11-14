package com.smarroquin.catalogomultimedia.repositories;

import com.smarroquin.catalogomultimedia.models.*;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Media_titlesRepository extends BaseRepository<Media_titles, Long> {
    @Override
    protected Class<Media_titles> entity() { return Media_titles.class; }
}
