package saaadel.linkedin.crawler.model.migration;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import saaadel.jpa.connection.JpaUnwrappedConnectionFactory;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;

public class LiquibaseDbMigration {

    private static final String DB_CHANGELOG = "liquibase/changelog/db.changelog.xml";

    private final Logger logger = LogManager.getLogger(this);

    public void update(Connection connection) {
        update(connection, true);
    }

    public void update(Connection connection, boolean closeDatabase) {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        final ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);

        final ResourceAccessor clFO = new ClassLoaderResourceAccessor();
        final ResourceAccessor fsFO = new FileSystemResourceAccessor();

        Database database = null;
        try {
            try {
                database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                final Liquibase liquibase = new Liquibase(DB_CHANGELOG, new CompositeResourceAccessor(clFO, fsFO, threadClFO), database);
                liquibase.update("");
            } finally {
                if (database != null && closeDatabase) {
                    database.close();
                }
            }
        } catch (LiquibaseException e) {
            throw new IllegalStateException(e);
        }
    }

    public void update(ClassLoader classLoader, EntityManager em, boolean resourceLocalTransactionType) throws SQLException {
        if (resourceLocalTransactionType) {
            em.getTransaction().begin();
            try {
                JpaUnwrappedConnectionFactory.getInstance(classLoader).doWork(em, connection -> new LiquibaseDbMigration().update(connection, false));
                em.getTransaction().commit();
            } catch (Exception ex) {
                try {
                    em.getTransaction().rollback();
                } catch (Exception exInner) {
                    ex.addSuppressed(exInner);
                }
                throw ex;
            }
        } else {
            JpaUnwrappedConnectionFactory.getInstance(classLoader).doWork(em, connection -> new LiquibaseDbMigration().update(connection, false));
        }
    }
}
