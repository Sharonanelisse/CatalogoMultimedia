package com.smarroquin.catalogomultimedia.services;

import com.smarroquin.catalogomultimedia.models.Media_titles;
import com.smarroquin.catalogomultimedia.repositories.mediaTitlesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class mediaTitlesService {

    @Inject
    mediaTitlesRepository mediaTitleRepo;

    public List<Media_titles> media_titles() { return mediaTitleRepo.findAll(); }

    public Media_titles guardar(Media_titles mt) {return mediaTitleRepo.guardar(mt);}

    public void eliminar(Media_titles mt) {
        mediaTitleRepo.eliminar(mt);}

}
