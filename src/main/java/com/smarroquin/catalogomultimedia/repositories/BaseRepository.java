package com.smarroquin.catalogomultimedia.repositories;

import com.smarroquin.catalogomultimedia.models.Media_titles_genres;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseRepository<T, ID> {
    @Inject
    private EntityManager em;

    protected abstract Class<T> entity();

    public T find(ID id) {return em.find(entity(), id);}

    public List<T> findAll() {
        String entityName = em.getMetamodel().entity(entity()).getName();

        return em.createQuery("select e from " + entityName + " e", entity())
                .getResultList();
    }

    public void tx(Consumer<EntityManager> work) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            work.accept(em);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public T guardar(T e) {
        final T[] result = (T[]) new Object[1]; // para capturar el resultado dentro del lambda

        tx(entityManager -> {
            Object id = entityManager.getEntityManagerFactory()
                    .getPersistenceUnitUtil()
                    .getIdentifier(e);
            if (id == null) {
                entityManager.persist(e);
                result[0] = e;
            } else {
                result[0] = entityManager.merge(e);
            }
        });

        return result[0];
    }


    public void eliminar(T e) {
        tx(entityManager -> entityManager.remove(
                entityManager.contains(e) ? e : entityManager.merge(e)
        ));
    }

    public Media_titles_genres findByTitleIdAndGenreId(Long titleId, Long genreId) {
        return null;
    }

}

