package saaadel.linkedin.crawler.web.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public enum WebDriverFactory {
    INSTANCE;

    private WebDriver webDriver;

    WebDriverFactory() {
    }

    public WebDriver getWebDriver() {
        if (this.webDriver == null) {
//            final HtmlUnitDriver webDriver = new HtmlUnitDriver() {
//                @Override
//                protected WebClient newWebClient(BrowserVersion version) {
//                    final WebClient webClient = super.newWebClient(version);
//                    webClient.getOptions().setThrowExceptionOnScriptError(false);
//                    webClient.getOptions().setPopupBlockerEnabled(true);
//                    webClient.getOptions().setCssEnabled(false);
//                    webClient.getOptions().setRedirectEnabled(true);
//                    webClient.getOptions().setUseInsecureSSL(true);
//                    return webClient;
//                }
//            };
//            webDriver.setJavascriptEnabled(true);
            final ChromeDriver webDriver_ = new ChromeDriver();
            this.webDriver = webDriver_;
        }
        return this.webDriver;
    }
}
