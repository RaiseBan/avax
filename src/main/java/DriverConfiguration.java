import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.io.File;
import java.util.logging.Level;

public class DriverConfiguration {
    public static WebDriver makeNewDriver(){
        String extensionPath = "C:\\Program Files\\ether_metamask-11.5.1.xpi";
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\geckodriver.exe");
        ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile profile = allProfiles.getProfile("default-release-1"); // Замените "myProfile" на название вашего профиля, если вы используете специфический
        profile.setPreference("layout.css.devPixelsPerPx", "0.75");
        profile.addExtension(new File(extensionPath));
        FirefoxOptions options = new FirefoxOptions();
//        LoggingPreferences logs = new LoggingPreferences();
//        logs.enable(LogType.BROWSER, Level.OFF); // Отключение логов браузера
//        logs.enable(LogType.DRIVER, Level.OFF); // Отключение логов драйвера
//        options.setCapability("goog:loggingPrefs", logs);

        options.setProfile(profile);
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(options);
    }

}
