package com.smarroquin.catalogomultimedia.repositories;

import com.smarroquin.catalogomultimedia.models.Media_files;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class mediaFilesRepository extends BaseRepository<Media_files, Long> {
    @Override
    protected Class<Media_files> entity() { return Media_files.class; }
}
