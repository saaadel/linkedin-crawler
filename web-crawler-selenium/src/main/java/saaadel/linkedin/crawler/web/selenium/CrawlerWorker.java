package saaadel.linkedin.crawler.web.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import saaadel.linkedin.crawler.model.dao.CompanyDao;
import saaadel.linkedin.crawler.model.dao.EmployeeDao;
import saaadel.linkedin.crawler.model.entity.Company;
import saaadel.linkedin.crawler.web.selenium.strategy.EmployeesCrawlingStrategy;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

public class CrawlerWorker implements Runnable {
    private final Logger logger = LogManager.getLogger(this);
    private final EntityManager em;
    private final WebDriver webDriver;
    private final EmployeesCrawlingStrategy employeesCrawlingStrategy;
    private final CompanyDao companyDao;
    private final EmployeeDao employeeDao;

    public CrawlerWorker(final EntityManager em, final WebDriver webDriver) {
        this.em = em;
        this.webDriver = webDriver;
        this.employeesCrawlingStrategy = new EmployeesCrawlingStrategy(webDriver);
        this.companyDao = new CompanyDao(em);
        this.employeeDao = new EmployeeDao(em);
    }

    @Override
    public void run() {
        Company company;
        logger.warn("Process companies - started");
        final FlushModeType previousFlushModeType = employeeDao.unwrapEntityManager().getFlushMode();
        employeeDao.unwrapEntityManager().setFlushMode(FlushModeType.AUTO);
        while ((company = companyDao.findNotProcessed()) != null) {
            employeeDao.unwrapEntityManager().getTransaction().begin();
            try {
                employeesCrawlingStrategy.fetch(company, employeeDao::persist, employeeDao::flush);
                companyDao.markAsProcessed(company.getId());
                companyDao.flush();
                employeeDao.unwrapEntityManager().getTransaction().commit();
            } catch (Exception ex) {
                employeeDao.unwrapEntityManager().getTransaction().rollback();
                throw new IllegalStateException(ex);
            }
        }
        employeeDao.unwrapEntityManager().setFlushMode(previousFlushModeType);
        logger.warn("Process companies - finished");
    }
}
