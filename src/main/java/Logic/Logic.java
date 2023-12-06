package Logic;

import Threadss.ThreadSafeStructure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.plaf.IconUIResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Logic {
    //    private final AtomicReference<List<Float>> observedPrice = new AtomicReference<>();
    private WebDriver driver;
    private ThreadSafeStructure threadSafeStructure;
    private String previousFlag = "";
    private static int loggerI = 0;
    public double mean;
    public double sko;
    private String mainWindowKey;
    private String metaMaskWindowKey;

    public Logic(WebDriver driver, ThreadSafeStructure threadSafeStructure) {
        this.driver = driver;
        this.threadSafeStructure = threadSafeStructure;
    }

    public String getMainWindowKey() {
        return mainWindowKey;
    }

    public void setMainWindowKey(String mainWindowKey) {
        this.mainWindowKey = mainWindowKey;
    }

    public String getMetaMaskWindowKey() {
        return metaMaskWindowKey;
    }

    public void setMetaMaskWindowKey(String metaMaskWindowKey) {
        this.metaMaskWindowKey = metaMaskWindowKey;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public void setSko(double sko) {
        this.sko = sko;
    }

    public double getSko() {
        return this.sko;
    }

    public double getMean() {
        return this.mean;
    }

    public void parseDimasCollection() {

        ArrayList<Float> floats = new ArrayList<>();
        List<List<String>> list = this.threadSafeStructure.get("greenSales");
        System.out.println("size1: " + list.size());
        for (List<String> array : list) {
            Float price = Float.parseFloat(array.get(0));
            floats.add(price);
        }
//        System.out.println("GreenMean: " + calculateMean(floats));

        ArrayList<Float> floats2 = new ArrayList<>();
        List<List<String>> list2 = this.threadSafeStructure.get("redSales");
//        System.out.println("size2: " + list2.size());
        for (List<String> array2 : list2) {
            Float price = Float.parseFloat(array2.get(0));
            System.out.println("redSale: " + price);
            floats2.add(price);

        }
//        System.out.println("RedMean: " + calculateMean(floats2));
        System.exit(1);
    }

    public ArrayList<Float> parseCollection() {

        ArrayList<Float> floats = new ArrayList<>();
        List<List<String>> list = this.threadSafeStructure.get(this.threadSafeStructure.getFlag());
        for (List<String> array : list) {
            Float price = Float.parseFloat(array.get(0));
            floats.add(price);
        }
        if (Objects.equals(previousFlag, "")) {
            this.previousFlag = this.threadSafeStructure.getFlag();
        } else {
            if (!Objects.equals(this.threadSafeStructure.getFlag(), previousFlag)) {
                System.out.println("[LOGGER]: Flag switched to " + this.threadSafeStructure.getFlag());
            }
        }
        System.err.println(floats);
        return floats;
    }


    public void startPriceChecking() {
        Thread priceCheckThread = new Thread(() -> {
            while (true) {
                double sellPrice;
                if (threadSafeStructure.getGlobalCount() >= 10) {

                    sellPrice = calculateMean(parseCollection());
                    System.out.println("Проверяем с Mean: " + sellPrice);
                    getActualPrice(sellPrice);
                } else {
                    sellPrice = this.mean;
                    System.out.println("Проверяем с Mean: " + sellPrice);
                    getActualPrice(sellPrice);
                }

                try {
                    Thread.sleep(1000); // Задержка между проверками
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        priceCheckThread.start();
    }


    public void getActualPrice(double sellPrice) {
        try {
            while (true) {

                loggerI++;
                refreshTable();
                WebDriverWait wait = new WebDriverWait(driver, 10);
                WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.MuiTable-root")));
                WebElement firstRow = table.findElement(By.cssSelector("tbody.MuiTableBody-root > tr[data-index='0']"));
                List<WebElement> cells = firstRow.findElements(By.cssSelector("td"));

                if (cells.size() > 2) {
                    WebElement thirdCell = cells.get(2);
                    String firstPrice = thirdCell.findElement(By.tagName("p")).getText();
//            if (loggerI == 1 || loggerI % 5 == 0){
//
//            }
                    System.out.println("[LOGGER]: Мат. ожидание: " + sellPrice);
                    System.out.println("[LOGGER]: Цена в таблице:" + firstPrice);
                    threadSafeStructure.setPause(true);
//            buyNFT(firstRow);
                    System.err.println("Мат ожидание: " + Float.parseFloat(firstPrice));
                    System.err.println("Если такая цена, то покупаем: : " + (sellPrice - (sellPrice * 0.02)));
                    if (Float.parseFloat(firstPrice) < sellPrice - (sellPrice * 0.02)) { //TODO: Поменять обратно цену! Float.parseFloat(firstPrice) < sellPrice - (sellPrice * 0.1)
                        buyNFT(firstRow);
//                driver.switchTo().window(this.mainWindowKey);
                    }
                    break;
                }
                break;
            }
        } catch (Exception e) {
            driver.navigate().refresh();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.err.println("err");
                System.exit(1);
            }

        }


    }

    public void buyNFT(WebElement row) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        row.findElement(By.cssSelector(".MuiStack-root.css-1cwzbie")).click();
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("sleep to buy");
        }
        driver.switchTo().newWindow(WindowType.WINDOW);
        driver.get("moz-extension://cf2db923-9646-47db-993a-9cf240939a6d/popup.html#confirm-transaction");


        while (true) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='page-container-footer-next']"))).click();
                System.err.println("Купили");
                break;
            } catch (Exception e) {
                System.err.println("trying again...");
            }
        }
        threadSafeStructure.setPause(false);
        driver.switchTo().window(mainWindowKey);
        System.exit(1);


    }

    public void refreshTable() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='refresh-collection-items']")));
        button.click();
        if (loggerI == 1 || loggerI % 5 == 0) {
            System.out.println("REFRESH TABLE");
        }

    }


    public void getNewSale() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.get("https://avax.hyperspace.xyz/collection/avax/2b052ded-2ee6-4005-86d9-c14e5cd609ad");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".MuiTypography-root.MuiTypography-body1.css-wh8dwp"))).click();
        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.MuiTable-root")));
        WebElement firstRow = table.findElement(By.cssSelector("tbody.MuiTableBody-root > tr[data-index='0']"));

    }


    public static double calculateMean(List<Float> values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        System.out.println(sum / values.size());
        return sum / values.size();
    }

    public static double calculateCorrectedStdDev(List<Float> values) {
        double mean = calculateMean(values);
        double sumOfSquares = 0.0;
        for (double value : values) {
            sumOfSquares += Math.pow(value - mean, 2);
        }
        System.out.println(Math.sqrt(sumOfSquares / (values.size() - 1)));
        return Math.sqrt(sumOfSquares / (values.size() - 1));
    }

}

