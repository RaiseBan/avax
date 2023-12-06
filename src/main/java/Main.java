import Logic.Logic;
import Threadss.ThreadSafeStructure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import table.DynamicTableHandler;
import table.SalesTablePrice;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        WebDriver mainDriver = DriverConfiguration.makeNewDriver();
        mainDriver.manage().window().maximize();
        Auntification auth = new Auntification(mainDriver);        DynamicTableHandler dynamicTableHandler = new DynamicTableHandler(mainDriver);
        WebDriverWait wait = new WebDriverWait(mainDriver, 10);





        HashMap<String, String> keysFromAuth = auth.login(); // 0 - returned; 1 - mainWindowKey; 2 - metaMaskWindowKey
        if (keysFromAuth.get("returned") != "1"){
            throw new RuntimeException("Неверная аунтификация metaMask");
        }
        System.out.println("ВСЕ ОК!");

//        String script = "fetch(\"https://mainnet-avax.hyperspace.xyz/\", {\n" +
//                "    \"credentials\": \"omit\",\n" +
//                "    \"headers\": {\n" +
//                "        \"User-Agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0\",\n" +
//                "        \"Accept\": \"*/*\",\n" +
//                "        \"Accept-Language\": \"ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3\",\n" +
//                "        \"content-type\": \"application/json\",\n" +
//                "        \"Sec-Fetch-Dest\": \"empty\",\n" +
//                "        \"Sec-Fetch-Mode\": \"cors\",\n" +
//                "        \"Sec-Fetch-Site\": \"same-site\"\n" +
//                "    },\n" +
//                "    \"referrer\": \"https://avax.hyperspace.xyz/\",\n" +
//                "    \"body\": \"{\\\"operationName\\\":\\\"GetCollectionActivity\\\",\\\"variables\\\":{\\\"condition\\\":{\\\"projects\\\":[{\\\"project_id\\\":\\\"0b942a2a-48be-4032-b2fd-a3c2251482d5\\\"}],\\\"action_types\\\":[\\\"TRANSACTION\\\"]},\\\"paginationInfo\\\":{\\\"page_number\\\":1,\\\"page_size\\\":30}},\\\"query\\\":\\\"query GetCollectionActivity($condition: GetCollectionActivityCondition!, $orderBy: [OrderConfig!], $paginationInfo: MPAPaginationConfig) {\\\\n  getCollectionActivity(\\\\n    condition: $condition\\\\n    pagination_info: $paginationInfo\\\\n    order_by: $orderBy\\\\n  ) {\\\\n    marketplace_snapshots {\\\\n      project_id\\\\n      token_address\\\\n      name\\\\n      metadata_img\\\\n      metadata_uri\\\\n      project_name\\\\n      project_image\\\\n      project_supply\\\\n      project_metadata\\\\n      is_project_verified\\\\n      project_slug\\\\n      project_description\\\\n      project_protocol\\\\n      created_at\\\\n      rarity_snapshot {\\\\n        hyperspace\\\\n        __typename\\\\n      }\\\\n      marketplace_state {\\\\n        token_address\\\\n        escrow_address\\\\n        trade_state\\\\n        tx_id\\\\n        instruction_index\\\\n        type\\\\n        seller_address\\\\n        buyer_address\\\\n        marketplace_fee_address\\\\n        marketplace_program_id\\\\n        marketplace_instance_id\\\\n        price\\\\n        fee\\\\n        amount\\\\n        seller_referral_address\\\\n        seller_referral_fee\\\\n        buyer_referral_address\\\\n        buyer_referral_fee\\\\n        metadata\\\\n        block_timestamp\\\\n        block_number\\\\n        block_index\\\\n        expiry_time\\\\n        currency_price\\\\n        decimal\\\\n        currency\\\\n        display_price\\\\n        __typename\\\\n      }\\\\n      non_marketplace_state {\\\\n        token_address\\\\n        tx_id\\\\n        instruction_index\\\\n        type\\\\n        token_name\\\\n        source_address\\\\n        destination_address\\\\n        program_id\\\\n        new_authority\\\\n        price\\\\n        currency\\\\n        currency_price\\\\n        amount\\\\n        decimal\\\\n        destination_token_account\\\\n        source_token_account\\\\n        sub_instruction_index\\\\n        metadata\\\\n        block_timestamp\\\\n        block_number\\\\n        __typename\\\\n      }\\\\n      __typename\\\\n    }\\\\n    pagination_info {\\\\n      current_page_number\\\\n      current_page_size\\\\n      has_next_page\\\\n      total_page_number\\\\n      __typename\\\\n    }\\\\n    __typename\\\\n  }\\\\n}\\\"}\",\n" +
//                "    \"method\": \"POST\",\n" +
//                "    \"mode\": \"cors\"\n" +
//                "})\n" +
//                ".then(response => {\n" +
//                "    if (response.ok) {\n" +
//                "        return response.json(); // Преобразование ответа в JSON\n" +
//                "    } else {\n" +
//                "        throw new Error('Network response was not ok.');\n" +
//                "    }\n" +
//                "})\n" +
//                ".then(data => {\n" +
//                "    console.log(data); // Здесь вы можете работать с данными JSON\n" +
//                "})\n" +
//                ".catch(error => {\n" +
//                "    console.error('There has been a problem with your fetch operation:', error);\n" +
//                "});\n";
//        ((JavascriptExecutor) mainDriver).executeScript(script);
//
//
//        System.exit(1);
        HashMap<String, List<List<String>>> salesList = dynamicTableHandler.getSalesList(); // берем мапу статической таблицы 0 - redSales; 1 - greenSales а дальше...
        Thread.sleep(3000);
        ThreadSafeStructure threadSafeStructure = new ThreadSafeStructure(salesList);
        Logic logic = new Logic(mainDriver, threadSafeStructure);
        logic.setMainWindowKey(keysFromAuth.get("mainWindowKey"));
        logic.setMetaMaskWindowKey(keysFromAuth.get("metaMaskWindowKey"));
        List<Float> floats = logic.parseCollection();
        System.out.println("Данные коллекции: ");
        Scanner scanner = new Scanner(System.in);
        Float mean = StatisticalFunctions.calculateMean(floats);
        Float SKO = StatisticalFunctions.calculateCorrectedStdDev(floats);
        boolean isPersonData = false;
        System.out.println("Мат. ожидание: " + mean);
        System.out.println("СКО: " + SKO);
        System.out.println("Отрезаем по верхней границе: " + (mean + SKO));
        System.err.println("Покупаем по: " + (mean - (mean * 0.02)));
        System.out.println("\n");
        System.out.println("Sales: ");
        System.out.println(floats);
        System.out.println("Продолжить с этими данными? yes/no");
        String res = "";
        while (true) {
            try {
                res = scanner.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Try one more  time: {yes / no}");
            }
        }

        if (Objects.equals(res, "no")){
            while (true) {
                try {
                    System.out.println("Введите свои данные: \n Мат. ожидание: ");
                    mean = Float.parseFloat(scanner.nextLine());
                    System.out.println(" СКО: ");
                    SKO = Float.parseFloat(scanner.nextLine());
                    threadSafeStructure.clearAllValues();
                    System.err.println("Покупаем по: " + (mean - (mean * 0.02)));
                    System.out.println("Starting...");
                    isPersonData = true;
                    break;
                } catch (Exception e) {
                    System.err.println("Try one more time! \nYou must use Float numbers!");
                }
            }
        }
        if (Objects.equals(res, "yes")){
            System.out.println("Starting...");
            threadSafeStructure.setGlobalCount(10);
        }
        System.out.println("Values: ");

        System.out.println("Мат. ожидание: " + mean);
        System.out.println("СКО: " + SKO);
        System.out.println("Отрезаем по верхней границе: " + (mean + SKO));
        System.out.println(threadSafeStructure.get("greenSales"));
        System.out.println(threadSafeStructure.get("redSales"));




        WebDriver tableDriver = DriverConfiguration.makeNewDriver();
        SalesTablePrice salesTablePrice = new SalesTablePrice(tableDriver, threadSafeStructure);
        if (isPersonData){
            salesTablePrice.setMean(mean);
            salesTablePrice.setSko(SKO);
            logic.setMean(mean);
            logic.setSko(SKO);
        }else{
            logic.setMean(mean);
            logic.setSko(SKO);
        }
        boolean pause = false;

        salesTablePrice.callNewThread();

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".MuiTypography-root.MuiTypography-body1.css-wh8dwp"))).click();
        System.out.println("sleeping");
        Thread.sleep(5000);
        System.out.println("working");
        logic.startPriceChecking();


    }
}
