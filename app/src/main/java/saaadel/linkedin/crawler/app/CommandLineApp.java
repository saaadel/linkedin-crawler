package saaadel.linkedin.crawler.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import saaadel.linkedin.crawler.model.PersistenceManager;
import saaadel.linkedin.crawler.model.migration.LiquibaseDbMigration;
import saaadel.linkedin.crawler.web.selenium.CrawlerWorker;
import saaadel.linkedin.crawler.web.selenium.WebDriverFactory;

import javax.persistence.EntityManager;
import java.sql.SQLException;

public class CommandLineApp implements Runnable {
    private final Logger logger = LogManager.getLogger(this);

    public static void main(String[] args) {
//        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

        System.setProperty("webdriver.chrome.driver", "C:\\dev\\selenium2.webdriver\\chromedriver.exe");

        new CommandLineApp().run();
    }

    public void run() {
        final EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
        try {
            this.runWithEntityManager(em);
        } finally {
            try {
                em.close();
            } finally {
                PersistenceManager.INSTANCE.close();
            }
        }
    }

    private void runWithEntityManager(final EntityManager em) {
        this.migrateDatabase(em);
        this.crawlEverything(em);
    }

    private void crawlEverything(final EntityManager em) {
        final WebDriver webDriver = WebDriverFactory.INSTANCE.getWebDriver();
        crawlEverything(em, webDriver);
    }

    private void crawlEverything(final EntityManager em, final WebDriver webDriver) {
        new CrawlerWorker(em, webDriver).run();
    }

    private void migrateDatabase(EntityManager em) {
        try {
            new LiquibaseDbMigration().update(this.getClass().getClassLoader(), em, true);
        } catch (SQLException ex) {
            logger.fatal("", ex);
            throw new IllegalStateException(ex);
        }
    }
}
