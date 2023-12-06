package table;

import Threadss.ThreadSafeStructure;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Array;
import java.util.*;

public class DynamicTableHandler {
    private List<Float> redSalesList = new ArrayList<>();
    private List<Float> greenSalesList = new ArrayList<>();
    private WebDriver driver;
    private WebDriver driverForCheckSale;
    WebDriverWait wait;
    private int requiredItemCount = 30;
    private Set<String> collectedPrices = new HashSet<>();

    public DynamicTableHandler(WebDriver defaultDriver) {
        this.driver = defaultDriver;
        this.wait = new WebDriverWait(driver, 10);
    }

    public HashMap<String, List<List<String>>> getSalesList() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".MuiTypography-root.MuiTypography-body1.css-wh8dwp"))).click();
        //MuiBox-root css-erqhsh
        return collectItems();
    }



    public HashMap<String, List<List<String>>> collectItems() throws InterruptedException {
        Thread.sleep(1000);
        WebElement body = driver.findElement(By.tagName("body"));
        body.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
        Thread.sleep(1000);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".MuiSkeleton-root")));

        // Получение HTML таблицы
        WebElement table = driver.findElement(By.cssSelector("table[class='MuiTable-root css-1v4vtoa']"));
        String tableHtml = table.getAttribute("outerHTML");

        // Парсинг HTML таблицы с Jsoup
        Document doc = Jsoup.parse(tableHtml);
        Elements rows = doc.select("tr[data-index]");

        int count = 0;
        List<List<String>> redSalesList = new ArrayList<>();
        List<List<String>> greenSalesList = new ArrayList<>();
        HashMap<String, List<List<String>>> comboList = new HashMap<>();

        for (Element row : rows) {
            Elements cells = row.select("td");
            if (cells.size() > 6) {
                Elements desiredClassCells = cells.select("div.MuiBox-root.css-1sbmayt");
                if (!desiredClassCells.isEmpty()) {
                    String redPrice = cells.get(2).text();

                    String rank = cells.get(3).text();
                    String buyer = cells.get(4).text();
                    String seller = cells.get(5).text();

                    HashMap<Integer, List<String>> tempHash = new HashMap<>();
                    List<String> temp = new ArrayList<>();
                    temp.add(redPrice);
                    temp.add(rank);
                    temp.add(buyer);
                    temp.add(seller);
                    redSalesList.add(temp);


                }
                else{
                    String greenPrice = cells.get(2).text();
                    String rank = cells.get(3).text();
                    String buyer = cells.get(4).text();
                    String seller = cells.get(5).text();

                    HashMap<Integer, List<String>> tempHash = new HashMap<>();
                    List<String> temp = new ArrayList<>();
                    temp.add(greenPrice);
                    temp.add(rank);
                    temp.add(buyer);
                    temp.add(seller);
                    greenSalesList.add(temp);
                }
            }
        }
        comboList.put("redSales", redSalesList);
        comboList.put("greenSales", greenSalesList);
        return comboList;
    }


}
