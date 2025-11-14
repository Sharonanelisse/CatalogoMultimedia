package com.smarroquin.catalogomultimedia.services;

import com.smarroquin.catalogomultimedia.models.Media_titles;
import com.smarroquin.catalogomultimedia.repositories.Media_titlesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class media_titlesService {

    @Inject
    Media_titlesRepository Media_titleRepo;

    public List<Media_titles> media_titles() {
        return Media_titleRepo.findAll(); }

    public Media_titles guardar(Media_titles mt) {return Media_titleRepo.guardar(mt);}

    public void eliminar(Media_titles mt) {
        Media_titleRepo.eliminar(mt);}

}
