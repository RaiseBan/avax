import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class Logic {
    private String actualPrice;
    private List<String> listOfPrices;
    private WebDriver driver;

    public Logic(WebDriver driver){
        this.driver = driver;
    }
    public String getActualPrice(){
        WebDriverWait wait = new WebDriverWait(driver, 10);
        String cssSelector = ".MuiBox-root.css-c5yelr";
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
        System.out.println("кликнули");
        element.click();
        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.MuiTable-root")));
        WebElement firstRow = table.findElement(By.cssSelector("tbody.MuiTableBody-root > tr[data-index='0']"));
        List<WebElement> cells = firstRow.findElements(By.cssSelector("td"));
        if (cells.size() > 2) {
            WebElement thirdCell = cells.get(2);
            String price = thirdCell.findElement(By.tagName("p")).getText();
            System.out.println("Price: " + price);
            this.actualPrice = price;
            return price;
        }
        return null;
    }
    public String getInstantlyPrice(){
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement pElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p.MuiTypography-root.MuiTypography-body1.css-1tmaw2r")));
        String text = pElement.getText();
        System.out.println(text);
        return null;
    }
    public String priceObserver(){
        JavascriptExecutor js = (JavascriptExecutor) driver;

// Находим элемент, за изменениями которого мы хотим наблюдать
        WebElement element = driver.findElement(By.cssSelector("p.MuiTypography-root.MuiTypography-body1.css-1tmaw2r"));

        String script = "var callback = arguments[arguments.length - 1];" +
                "var elem = arguments[0];" +
                "var observer = new MutationObserver(function(mutations) {" +
                "  mutations.forEach(function(mutation) {" +
                "    if (mutation.type === 'childList' || mutation.type === 'characterData') {" +
                "      var newText = elem.textContent || elem.innerText;" +
                "      observer.disconnect();" + // Отключаем наблюдатель после первого изменения
                "      callback(newText);" + // Передаем новый текст в Java-код
                "    }" +
                "  });" +
                "});" +
                "var config = { childList: true, characterData: true, subtree: true };" + // subtree: true для отслеживания вложенных элементов
                "observer.observe(elem, config);";

// Выполняем скрипт с помощью JavascriptExecutor
        String changedText = (String) js.executeAsyncScript(script, element);

// Выводим измененный текст
        System.out.println("Измененный текст: " + changedText);
        return changedText;
    }
    public void refreshTable(){

    }
}
