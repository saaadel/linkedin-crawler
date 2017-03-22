package saaadel.linkedin.crawler.model.dao;

import javax.persistence.EntityManager;

public class AbstractDao<EntityType, PrimaryKeyType> implements Dao<EntityType, PrimaryKeyType> {
    private final EntityManager em;
    private final Class<EntityType> entityClass;

    public AbstractDao(EntityManager em, Class<EntityType> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    @Override
    public final EntityManager unwrapEntityManager() {
        return em;
    }

    @Override
    public final Class<EntityType> getEntityClass() {
        return entityClass;
    }
}
