package saaadel.linkedin.crawler.web.selenium.strategy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import saaadel.linkedin.crawler.model.entity.Company;
import saaadel.linkedin.crawler.model.entity.Employee;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmployeesCrawlingStrategy {

    private static final String LINKEDIN_SIGNIN_URL = "https://www.linkedin.com/uas/login";
    private static final String AT_TITLE_COMPANY_SEPARATOR = " at ";
    private static final int BOTTOM_SCROLLING_READY_TIMEOUT = 1000;
    private static final int SIGNIN_READY_TIMEOUT = 5000;
    private static final int SEARCH_RESULTS_READY_TIMEOUT = 500;

    /**
     * Key - normalized, used in search form
     * Values - other forms - rules, used to collect from text
     */
    private static Multimap<String, Pattern> FILTER_TITLE_MAP = HashMultimap.create();
    private static HashMap<String, String> NORMALIZED_TITLE_MAP = new HashMap<>();

    static {
        FILTER_TITLE_MAP.put("CEO", Pattern.compile("(?si)^.*(\\bCEO\\b).*$"));
        FILTER_TITLE_MAP.put("CEO", Pattern.compile("(?si)^.*(\\bChief\\s+Executive\\s+Officer\\b).*$"));
        FILTER_TITLE_MAP.put("CEO", Pattern.compile("(?si)^.*(\\bManaging\\s+Director\\b).*$"));
        FILTER_TITLE_MAP.put("CEO", Pattern.compile("(?si)^.*(\\bExecutive\\s+Director\\b).*$"));

        FILTER_TITLE_MAP.put("CEO", Pattern.compile("(?si)^.*(\\bPresident\\b).*$"));
//        FILTER_TITLE_MAP.put("CEO", Pattern.compile("(?si)^.*(\\bVice(?:-|\\s+)President\\b).*$")); // president overlap this rule
        FILTER_TITLE_MAP.put("CEO", Pattern.compile("(?si)^.*(\\bFounder\\b).*$"));

        FILTER_TITLE_MAP.put("CTO", Pattern.compile("(?si)^.*(\\bCTO\\b).*$"));
        FILTER_TITLE_MAP.put("CTO", Pattern.compile("(?si)^.*(\\bChief\\s+technical\\s+officer\\b).*$"));

        FILTER_TITLE_MAP.put("COO", Pattern.compile("(?si)^.*(\\bCOO\\b).*$"));
        FILTER_TITLE_MAP.put("COO", Pattern.compile("(?si)^.*(\\bChief\\s+Operating\\s+Officer\\b).*$"));

        FILTER_TITLE_MAP.put("Departmental manager", Pattern.compile("(?si)^.*(\\bDepartmental\\s+manager\\b).*$"));

        FILTER_TITLE_MAP.put("General manager", Pattern.compile("(?si)^.*(\\bGeneral\\s+manager\\b).*$"));

        FILTER_TITLE_MAP.put("Divisional manager", Pattern.compile("(?si)^.*(\\bDivisional\\s+manager\\b).*$"));

        FILTER_TITLE_MAP.put("Regional manager", Pattern.compile("(?si)^.*(\\bRegional\\s+manager\\b).*$"));

        FILTER_TITLE_MAP.put("Chairman", Pattern.compile("(?si)^.*(\\bChairman\\b).*$"));

        FILTER_TITLE_MAP.put("Development director", Pattern.compile("(?si)^.*(\\bDevelopment\\s+director\\b).*$"));
        FILTER_TITLE_MAP.put("Development director", Pattern.compile("(?si)^.*(\\bDirector\\s+of\\s+development\\b).*$"));

        FILTER_TITLE_MAP.put("Director Business Development", Pattern.compile("(?si)^.*(\\bDirector\\s+Business\\s+Development\\b).*$"));

        NORMALIZED_TITLE_MAP.put("CEO", "CEO");
        NORMALIZED_TITLE_MAP.put("CTO", "CTO");
        NORMALIZED_TITLE_MAP.put("COO", "COO");
        NORMALIZED_TITLE_MAP.put("Departmental manager", "Departmental manager");
        NORMALIZED_TITLE_MAP.put("General manager", "General manager");
        NORMALIZED_TITLE_MAP.put("Divisional manager", "Divisional manager");
        NORMALIZED_TITLE_MAP.put("Regional manager", "Regional manager");
        NORMALIZED_TITLE_MAP.put("Chairman", "Chairman");
        NORMALIZED_TITLE_MAP.put("Development director", "Development director");
        NORMALIZED_TITLE_MAP.put("Director Business Development", "Director Business Development");
    }

    private final Logger logger = LogManager.getLogger(this);
    private WebDriver webDriver;

    public EmployeesCrawlingStrategy(WebDriver driver) {
        this.webDriver = driver;
    }

    private static boolean isRulesMatches(String str, Collection<Pattern> rules) {
        if (str != null) {
            for (final Pattern rule : rules) {
                if (rule.matcher(str).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void fetch(Company company, Consumer<Employee> employeeDbSaverFI, Runnable entityManagerFlushFI) {
        logger.warn("Process company: {}\t{}", company.getName(), company.getLink());
        final Long companyId = getCompanyId(company);
        if (companyId != null) {
            for (final String title : new HashSet<>(FILTER_TITLE_MAP.keys())) {
                final String normalizedTitle = NORMALIZED_TITLE_MAP.get(title);
                final Collection<Pattern> titleRules = FILTER_TITLE_MAP.get(title);
                int page = 1;
                for (; ; ) {
                    logger.debug("Process page {} for {} at {}", page, title, company.getName());
                    final List<Employee> employees = fetchAllEmployeesOnPage(company, title, page);
                    if (employees.isEmpty()) {
                        break;
                    } else {
                        //filter by title, and set normalized title
                        employees
                                .stream()
                                .filter(emp -> {
                                    if (StringUtils.stripToEmpty(emp.getProfileCompany()).toLowerCase().contains(StringUtils.stripToEmpty(company.getName()).toLowerCase()) &&
                                            isRulesMatches(emp.getProfileTitle(), titleRules)) {
                                        emp.setNormalizedTitle(normalizedTitle);
                                        return true;
                                    } else if (StringUtils.stripToEmpty(emp.getCurrentCompany()).toLowerCase().contains(StringUtils.stripToEmpty(company.getName()).toLowerCase()) &&
                                            isRulesMatches(emp.getCurrentTitle(), titleRules)) {
                                        emp.setNormalizedTitle(normalizedTitle);
                                        return true;
                                    }
                                    return false;
                                })
                                .peek(emp -> logger.info("Saving: {}", emp))
                                .forEach(employeeDbSaverFI);

                        entityManagerFlushFI.run();

                        ++page;
                    }
                }
            }
        }
    }

    private Long getCompanyId(Company company) {
        Long companyLinkedInId = null;
        if (StringUtils.isNotEmpty(company.getLink())) {
            final String companyLinkedInIdStr = company.getLink().replaceAll("^https\\:\\/\\/www\\.linkedin\\.com/company/(\\d+)?.*$", "$1");
            try {
                companyLinkedInId = Long.valueOf(companyLinkedInIdStr, 10);
            } catch (NumberFormatException ex) {
            }
        }
        return companyLinkedInId;
    }

    private void scrollPageBottom() {
        try {
            Thread.sleep(SEARCH_RESULTS_READY_TIMEOUT);
        } catch (InterruptedException ignored) {
        }
        final WebElement body = webDriver.findElement(By.tagName("body"));
        Actions scrollDownMacro = new Actions(webDriver);
        for (int i = 0; i < 15; ++i) {
            scrollDownMacro = scrollDownMacro.sendKeys(body, Keys.PAGE_DOWN);
        }

        final Action scrollDown = scrollDownMacro.build();
        scrollDown.perform();
    }

    private boolean loadAllEmployeesPage(Company company, String title, int page) {
        final Long companyId = getCompanyId(company);
        if (companyId != null) {
            String employeesUrl = MessageFormat.format("https://www.linkedin.com/search/results/people/?facetCurrentCompany=%5B%22{0,number,#}%22%5D", companyId);
            if (StringUtils.isNotEmpty(company.getName())) {
                employeesUrl += "&company=" + company.getName();
            }
            employeesUrl += "&origin=FACETED_SEARCH&title=" + title.replace(" ", "%20");
            if (page > 1) {
                employeesUrl += "&page=" + page;
            }
            logger.trace("Location change: {}", employeesUrl);
            webDriver.get(employeesUrl);
            if (loginIfNeeded()) {
                return loadAllEmployeesPage(company, title, page);
            }

            if (webDriver.findElements(By.className("premium-btn")).size() == 0) {

                //loaded - check if we have result items
                scrollPageBottom();
                try {
                    Thread.sleep(BOTTOM_SCROLLING_READY_TIMEOUT);
                } catch (InterruptedException ignored) {
                }

                return webDriver.findElements(By.className("search-no-results__message")).size() == 0;
            } else {
                throw new IllegalStateException("PREMIUM REQUIRED");
            }
        }
        return false;
    }

    private List<Employee> fetchAllEmployeesOnPage(Company company, String searchForTitle, int page) {
        final boolean hasItems = loadAllEmployeesPage(company, searchForTitle, page);
        if (hasItems) {
            return webDriver.findElements(By.className("search-result__wrapper"))
                    .stream()
                    .map(el -> {
                        final Employee employee = new Employee();
                        employee.setId(null);
                        employee.setProcessed(false);
                        employee.setChecked(false);

                        employee.setCompanyId(company.getId());

                        employee.setLink(el.findElement(By.className("search-result__result-link")).getAttribute("href"));
                        employee.setName(el.findElement(By.className("actor-name")).getText());
                        try {
                            el.findElement(By.className("premium-icon"));
                            employee.setIsPremium(true);
                        } catch (NoSuchElementException ignored) {
                            employee.setIsPremium(false);
                        }
                        try {
                            final String sublineLevel1 = el.findElement(By.className("subline-level-1")).getText();
                            if (sublineLevel1.endsWith(company.getName())) {
                                employee.setProfileTitle(sublineLevel1.substring(0, sublineLevel1.length() - company.getName().length() - AT_TITLE_COMPANY_SEPARATOR.length()));
                                employee.setProfileCompany(company.getName());
                            } else {
                                final int separatorIndex = sublineLevel1.lastIndexOf(AT_TITLE_COMPANY_SEPARATOR);
                                if (separatorIndex > -1) {
                                    employee.setProfileTitle(sublineLevel1.substring(0, separatorIndex));
                                    try {
                                        employee.setProfileCompany(sublineLevel1.substring(separatorIndex + AT_TITLE_COMPANY_SEPARATOR.length()));
                                    } catch (IndexOutOfBoundsException ignored) {
                                        employee.setProfileCompany("");
                                    }
                                }
                            }
                        } catch (NoSuchElementException ignored) {
                            employee.setProfileTitle("");
                            employee.setProfileCompany("");
                        }

                        try {
                            final String sublineLevel2 = el.findElement(By.className("subline-level-2")).getText();
                            employee.setLocation(sublineLevel2);
                        } catch (NoSuchElementException ignored) {
                            employee.setLocation("");
                        }

                        try {
                            final WebElement lineElem = el.findElement(By.className("search-result__snippets mt2"));
                            try {
                                employee.setCurrentCompany(lineElem.findElement(By.tagName("strong")).getText());
                                final String currentTitle = lineElem.getText().trim().replaceAll(
                                        "(?si)^Current:\\s+(.*)\\sat\\s" + Pattern.quote(employee.getCurrentCompany()) + "$",
                                        "$1");
                                employee.setCurrentTitle(currentTitle);
                            } catch (NoSuchElementException ignored) {
                                final String currentTitle = lineElem.getText().trim().replaceAll(
                                        "(?si)^Current:\\s+(.*)$", //TODO check if " at " present here
                                        "$1");
                                employee.setCurrentTitle(currentTitle);
                                employee.setCurrentCompany("");
                            }
                        } catch (NoSuchElementException ignored) {
                            employee.setCurrentTitle("");
                            employee.setCurrentCompany("");
                        }

                        try {
                            employee.setConnectionsLvl(el.findElement(By.className("distance-badge")).findElement(By.className("dist-value")).getText());
                        } catch (NoSuchElementException ignored) {
                            employee.setConnectionsLvl(null);
                        }

                        employee.setEmail(null); // can't get, only parse all profile
                        employee.setAbout(null); // from all profile
                        employee.setWebsite(null); // can't get, only parse all profile OR from all profile
                        employee.setConnectionsCount(null); // can't get, only parse all profile
                        return employee;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void login() {
        webDriver.get(LINKEDIN_SIGNIN_URL);
        final WebElement session_key = webDriver.findElement(By.name("session_key"));
        final WebElement session_password = webDriver.findElement(By.name("session_password"));
        final WebElement signin = webDriver.findElement(By.name("signin"));

        session_key.sendKeys(LinkedCreds.LINKED_IN_EMAIL);
        session_password.sendKeys(LinkedCreds.LINKED_IN_PASSWORD);
        signin.click();

        try {
            Thread.sleep(SIGNIN_READY_TIMEOUT);
        } catch (InterruptedException ignored) {
        }

        if (LINKEDIN_SIGNIN_URL.equals(webDriver.getCurrentUrl())) {
            final WebElement body = webDriver.findElement(By.tagName("body"));
            final String bodyInnerHTML = body.getAttribute("innerHTML");
            throw new IllegalStateException(MessageFormat.format("Incorrect email/password? In BODY:\n{0}", bodyInnerHTML));
        }
    }

    private boolean loginIfNeeded() {
        if ("https://www.linkedin.com/login/".equals(webDriver.getCurrentUrl())) {
            login();
            return true;
        }
        return false;
    }
}
