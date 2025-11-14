package com.smarroquin.catalogomultimedia.services;

import com.smarroquin.catalogomultimedia.models.*;
import com.smarroquin.catalogomultimedia.repositories.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class Media_filesService {

    @Inject
    Media_filesRepository Media_filesRepo;

    public List<Media_files> media_files() { return Media_filesRepo.findAll(); }

    public Media_files guardar(Media_files mf) {return Media_filesRepo.guardar(mf);}

    public void eliminar(Media_files mf) {Media_filesRepo.eliminar(mf);}

}
