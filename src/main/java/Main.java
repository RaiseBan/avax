import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.setProperty("webdriver.gecko.driver", "C:\\geckodriver.exe");
        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();
        driver.get("https://avax.hyperspace.xyz/collection/avax/2b052ded-2ee6-4005-86d9-c14e5cd609ad");
        Logic logic = new Logic(driver);
        logic.getActualPrice();
        logic.getInstantlyPrice();










    }
}
