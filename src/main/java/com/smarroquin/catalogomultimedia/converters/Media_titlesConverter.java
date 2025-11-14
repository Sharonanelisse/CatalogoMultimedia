package com.smarroquin.catalogomultimedia.converters;

import com.smarroquin.catalogomultimedia.models.Media_titles;
import com.smarroquin.catalogomultimedia.repositories.Media_titlesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;


@FacesConverter(value = "media_titlesConverter", managed = true)
@ApplicationScoped
public class Media_titlesConverter implements Converter<Media_titles> {

    @Inject
    Media_titlesRepository repo;

    @Override
    public Media_titles getAsObject(FacesContext context, UIComponent component, String value) {
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
    public String getAsString(FacesContext context, UIComponent component, Media_titles value) {
        return (value == null || value.getMedia_titles_id() ==null) ? "" : value.getMedia_titles_id().toString();
    }
}
