package saaadel.linkedin.crawler.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

public interface Dao<EntityType, PrimaryKeyType> {
    EntityManager unwrapEntityManager();

    Class<EntityType> getEntityClass();

    default void persist(EntityType entity) {
        unwrapEntityManager().persist(entity);
    }

    default void merge(EntityType entity) {
        unwrapEntityManager().merge(entity);
    }

    default void remove(EntityType entity) {
        unwrapEntityManager().remove(entity);
    }

    default void detach(EntityType entity) {
        unwrapEntityManager().detach(entity);
    }

    default EntityType reference(PrimaryKeyType entityId) {
        return unwrapEntityManager().getReference(getEntityClass(), entityId);
    }

    default EntityType find(PrimaryKeyType entityId, LockModeType lockModeType) {
        return unwrapEntityManager().find(getEntityClass(), entityId, lockModeType);
    }

    default EntityType find(PrimaryKeyType entityId) {
        return unwrapEntityManager().find(getEntityClass(), entityId);
    }

    default void flush() {
        unwrapEntityManager().flush();
    }
}
