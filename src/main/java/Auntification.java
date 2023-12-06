import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Auntification {
    WebDriver mainDriver;

    public Auntification(WebDriver driver) {
        this.mainDriver = driver;
    }

    public HashMap<String, String> login() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(mainDriver, 10);
        mainDriver.manage().window().maximize();
        //https://avax.hyperspace.xyz/collection/avax/5ad14893-3f7e-4be2-9205-d2122591c9f2 - хуйня какая-та
        //https://avax.hyperspace.xyz/collection/avax/0b942a2a-48be-4032-b2fd-a3c2251482d5 - Tiger
        //https://avax.hyperspace.xyz/collection/avax/2b052ded-2ee6-4005-86d9-c14e5cd609ad - наша
        mainDriver.get("https://avax.hyperspace.xyz/collection/avax/93bc03e3-6b70-4be7-8823-5d61ec35736d");
        String originalWindow = mainDriver.getWindowHandle();
        mainDriver.switchTo().newWindow(WindowType.TAB);

        mainDriver.get("moz-extension://cf2db923-9646-47db-993a-9cf240939a6d/popup.html");
        String metaMaskWindow = mainDriver.getWindowHandle();
//        Thread.sleep(3000);
        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#password")));
        password.sendKeys("Vankomat33157");
        //unlock-submit
//        Thread.sleep(3000);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='unlock-submit']")));
        button.click();
        try {
            WebElement confirm = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button.btn--rounded.btn-primary")));
            confirm.click();
        } catch (Exception e) {
            System.out.println("Click is skipped");
        }
//        button btn--rounded btn-primary
        mainDriver.switchTo().window(originalWindow);
        //MuiButtonBase-root MuiButton-root MuiButton-text MuiButton-textPrimary MuiButton-sizeMedium MuiButton-textSizeMedium MuiButton-root MuiButton-text MuiButton-textPrimary MuiButton-sizeMedium MuiButton-textSizeMedium css-1df7oju
        WebElement connectWallet = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".MuiButtonBase-root.MuiButton-root.MuiButton-text.MuiButton-textPrimary.MuiButton-sizeMedium.MuiButton-textSizeMedium.MuiButton-root.MuiButton-text.MuiButton-textPrimary.MuiButton-sizeMedium.MuiButton-textSizeMedium.css-1df7oju")));
        connectWallet.click();
        //rk-wallet-option-metaMask
        try{
            System.err.println("sleeping");
            WebElement chooseMetaMask = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='rk-wallet-option-metaMask']")));
            chooseMetaMask.click();

            mainDriver.switchTo().window(metaMaskWindow);
            mainDriver.navigate().refresh();
            //button btn--rounded btn-primary page-container__footer-button
            WebElement setConnection = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button.btn--rounded.btn-primary.page-container__footer-button")));
            setConnection.click();
            //button btn--rounded btn-primary page-container__footer-button
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn--rounded.btn-primary.page-container__footer-button"))).click();


            mainDriver.switchTo().window(originalWindow);
        }catch (Exception e){
            System.out.println("skip...");
            mainDriver.switchTo().window(originalWindow);
            mainDriver.navigate().refresh();
            Thread.sleep(2000);
        }

        HashMap<String, String> answer = new HashMap<>();
        answer.put("returned", "1");
        answer.put("mainWindowKey", originalWindow);
        answer.put("metaMaskWindowKey", metaMaskWindow);

        return answer;
    }
}
