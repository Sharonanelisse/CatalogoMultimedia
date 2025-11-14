package com.smarroquin.catalogomultimedia.services;

import com.smarroquin.catalogomultimedia.models.Media_files;
import com.smarroquin.catalogomultimedia.repositories.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class mediaFilesService {

    @Inject
    mediaFilesRepository mediaFilesRepo;

    public List<Media_files> media_files() { return mediaFilesRepo.findAll(); }

    public Media_files guardar(Media_files mf) {return mediaFilesRepo.guardar(mf);}

    public void eliminar(Media_files mf) {mediaFilesRepo.eliminar(mf);}

}
