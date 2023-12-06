package table;

import Logic.Logic;
import Threadss.ThreadSafeStructure;
import org.apache.hc.core5.http.io.SessionOutputBuffer;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SalesTablePrice {
    private ThreadSafeStructure threadSafeStructure;
    private WebDriver driver;
    private String previousFlag = "";
    private Float mean;
    private Float sko;



    public SalesTablePrice(WebDriver driver, ThreadSafeStructure threadSafeStructure) {
        this.driver = driver;
        this.threadSafeStructure = threadSafeStructure;
    }
    public void setMean(Float mean){
        this.mean = mean;
    }
    public void setSko(Float sko){
        this.sko = sko;
    }
    public double getSko(){
        return this.sko;
    }
    public double getMean(){
        return this.mean;
    }


    public void callNewThread() {

        Thread newSaleThread = new Thread(() -> {
            try {
                takeNewSale();
            } catch (InterruptedException e) {
                // Обрабатываем прерывание потока, если необходимо
                Thread.currentThread().interrupt();
                System.out.println("Поток был прерван");
            }
        });
        newSaleThread.start(); // Запускаем поток
    }
    public boolean isNullOrWhiteSpace(String str) {
        return str == null || str.trim().isEmpty();
    }
    public ArrayList<Float> parseCollection(){

        ArrayList<Float> floats = new ArrayList<>();
        List<List<String>> list = this.threadSafeStructure.get(this.threadSafeStructure.getFlag());
        for (List<String> array: list) {
            Float price = Float.parseFloat(array.get(0));
            floats.add(price);
        }
        if (Objects.equals(previousFlag, "")){
            this.previousFlag = this.threadSafeStructure.getFlag();
        }else{
            if (!Objects.equals(this.threadSafeStructure.getFlag(), previousFlag)){
                System.out.println("[LOGGER]: Flag switched to " + this.threadSafeStructure.getFlag());
            }
        }
        return floats;
    }

    public void takeNewSale() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // https://avax.hyperspace.xyz/collection/avax/5ad14893-3f7e-4be2-9205-d2122591c9f2 - не хуйня какая-та
        // https://avax.hyperspace.xyz/collection/avax/2b052ded-2ee6-4005-86d9-c14e5cd609ad --  наша
        // https://avax.hyperspace.xyz/collection/avax/0b942a2a-48be-4032-b2fd-a3c2251482d5 - tiger
        driver.get("https://avax.hyperspace.xyz/collection/avax/93bc03e3-6b70-4be7-8823-5d61ec35736d");
        System.out.println("NEW DRIVER WORKING...");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".MuiTypography-root.MuiTypography-body1.css-wh8dwp"))).click();

        long lastActionTime = System.currentTimeMillis();
        String color;
        int greenCount = threadSafeStructure.get("greenSales").size();;
        int redCount = threadSafeStructure.get("redSales").size();;
        boolean breakFlag = false;
//        System.out.println("gSize: " + greenCount);
//        System.out.println("rSize: " + redCount);
//        System.out.println("gSalesList: " + threadSafeStructure.get("greenSales"));
//        System.out.println("rSalesList: " + threadSafeStructure.get("redSales"));
        while (true) {
            if ((System.currentTimeMillis() - lastActionTime) >= 20000) {
                performPeriodicAction();
                lastActionTime = System.currentTimeMillis();
                Thread.sleep(2000);
            }

            try {
                List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("tr[data-index]")));
                if (threadSafeStructure.getGlobalCount() >= 10){
                    System.err.println(threadSafeStructure.getGlobalCount());
                    System.err.println("switch...");
                    this.mean = null;
                    this.sko = null;
                }
                for (int i = 0; i < Math.min(16, rows.size()); i++) {
                    if (threadSafeStructure.isPause()){
                        Thread.sleep(5000);
                        while (true){
                            if (!threadSafeStructure.isPause()){
                                break;
                            }
                            Thread.sleep(5000);
                        }


                    }
//                    System.out.println("MaRow: " + rows.get(i).getText());
                    if (isNullOrWhiteSpace(rows.get(i).getText())){break;}
                    List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                    System.out.println("gSalesList: " + threadSafeStructure.get("greenSales"));
                    System.out.println("rSalesList: " + threadSafeStructure.get("redSales"));
                    if (cells.size() > 6) {
                        String value2 = cells.get(2).getText(); // значение столбца 2
                        String value3 = cells.get(3).getText(); // значение столбца 3
                        String value4 = cells.get(4).getText(); // значение столбца 4
                        String value5 = cells.get(5).getText(); // значение столбца 5

                        if ((value2  == null || value2.equals("") || value3 == null || value3.equals("") || value4 == null || value4.equals("") || value5 == null || value5.equals(""))){
                            continue;
                        }
                        if (sko == null || mean == null){
//                            System.err.println("program mean: " + Logic.calculateCorrectedStdDev(parseCollection()));
                            if (Float.parseFloat(value2) > Logic.calculateCorrectedStdDev(parseCollection()) + Logic.calculateMean(parseCollection())) {
                                continue;
                            }
                        }else{
//                            System.err.println("person mean: " + (mean + sko));
                            if (Float.parseFloat(value2) > mean + sko || Float.parseFloat(value2) < mean - sko){
                                continue;
                            }
                        }
                        try {
                            // Пытаемся найти элемент с классом 'MuiBox-root css-1sbmayt'
                            cells.get(6).findElement(By.cssSelector(".MuiBox-root.css-1sbmayt"));
                            // Если элемент найден, выводим "red"
//                        System.out.println("red");
                            color = "redSales";
                            redCount += 1;
                        } catch (NoSuchElementException e) {
                            color = "greenSales";
                            greenCount += 1;
//                        System.out.println("green");
                        }
                        if (greenCount >= redCount){
                            threadSafeStructure.setFlag("greenSales");
//                            System.err.println("switch to green");
                        }else{
                            threadSafeStructure.setFlag("redSales");
//                            System.err.println("switch to red");
                        }

                        List<List<String>> mainCollection = threadSafeStructure.get(color);
                        if (threadSafeStructure.get(color).size() <= 15){
                            ArrayList<String> list = new ArrayList<String>() {{
                                add(value2);
                                add(value3);
                                add(value4);
                                add(value5);
                            }};
//                            System.out.println("values: " + value2 + " " + value3 +  " " + value4 + " "  + value5 + " ");
//                            System.out.println((Objects.equals(mainCollection.get(i).get(1), value3)));
//                            System.out.println((Objects.equals(mainCollection.get(i).get(2), value4)));
//                            System.out.println((Objects.equals(mainCollection.get(i).get(3), value5)));
//
//                            System.out.println(!(Objects.equals(mainCollection.get(i).get(1), value3) &&
//                                    Objects.equals(mainCollection.get(i).get(2), value4) && Objects.equals(mainCollection.get(i).get(3), value5)));

                            if (threadSafeStructure.get(color).size() == 0){
//                                System.out.println("new sell1! " + list);
//                                System.out.println(mainCollection);
//                                System.out.println(color);
                                threadSafeStructure.addNewListByKey(color, list);
                                threadSafeStructure.incGlobalCount();

                            }else{
                                try {
                                    if (!(Objects.equals(mainCollection.get(i).get(1), value3) &&
                                            Objects.equals(mainCollection.get(i).get(2), value4) && Objects.equals(mainCollection.get(i).get(3), value5))) {
                                        System.out.println("new sell1! " + list);
                                        System.out.println(mainCollection);
                                        System.out.println(color);
                                        threadSafeStructure.addNewListByKey(color, list);
                                        threadSafeStructure.incGlobalCount();
                                    } else {
//                                breakFlag = true; // May be baby
//                                System.out.println("Braking bad1");
                                        break;

                                    }
                                }catch (IndexOutOfBoundsException e){
//                                    System.out.println("new sell1! " + list);
//                                    System.out.println(mainCollection);
//                                    System.out.println(color);
                                    threadSafeStructure.addNewListByKeyLast(color, list);
                                    threadSafeStructure.incGlobalCount();

//                                    System.err.println("IndexOutof ...");
                                }
                            }

                        }else{
                            threadSafeStructure.removeLastFromList(color);
                            ArrayList<String> list = new ArrayList<>() {{
                                add(value2);
                                add(value3);
                                add(value4);
                                add(value5);
                            }};
                            if (!(Objects.equals(mainCollection.get(i).get(1), value3) &&
                                    Objects.equals(mainCollection.get(i).get(2), value4) && Objects.equals(mainCollection.get(i).get(3), value5))) {

                                System.out.println("new sell2! " + list);
                                System.out.println(mainCollection);
                                System.out.println(color);
                                threadSafeStructure.addNewListByKey(color, list);
                                threadSafeStructure.incGlobalCount();
                            }
                            else{
//                                System.out.println("Braking bad2");
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
//                System.out.println("Обновление таблицы...");
//                e.printStackTrace();
                Thread.sleep(2000);
            }
        }
    }



    private void performPeriodicAction() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".MuiTypography-root.MuiTypography-body1.css-wh8dwp"))).click();
        Thread.sleep(1500);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".MuiTypography-root.MuiTypography-body1.css-wh8dwp"))).click();
        Thread.sleep(1500);
    }
}
