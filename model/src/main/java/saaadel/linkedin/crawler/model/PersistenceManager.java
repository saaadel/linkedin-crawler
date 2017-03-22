package saaadel.linkedin.crawler.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceManager {
    INSTANCE;

    private static final String PERSISTENCE_UNIT_NAME = "VEASTec";

    private EntityManagerFactory emFactory;

    private PersistenceManager() {
        // override connection config via sys properties
        emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, System.getProperties());
    }

    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public void close() {
        emFactory.close();
    }
}
