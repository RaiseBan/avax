package Threadss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeStructure {
    private final ConcurrentHashMap<String, List<List<String>>> complexMap;
    private boolean pause = false;
    private final Lock lock = new ReentrantLock();
    private final AtomicReference<String> flag = new AtomicReference<>("greenSales");
    private final AtomicInteger globalCount = new AtomicInteger(0);

    public ThreadSafeStructure(HashMap<String, List<List<String>>> initialMap) {
        this.complexMap = new ConcurrentHashMap<>();
        for (String key : initialMap.keySet()) {
            // Создаем новый список списков для каждого ключа
            List<List<String>> newList = new ArrayList<>();
            for (List<String> list : initialMap.get(key)) {
                // Копируем каждый список из исходной карты
                newList.add(new ArrayList<>(list));
            }
            this.complexMap.put(key, newList);
        }
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void setFlag(String flag){
        this.flag.set(flag);
    }

    public String getFlag(){
        return this.flag.get();
    }

    public int getGlobalCount(){
        return this.globalCount.get();
    }

    public void incGlobalCount(){
        this.globalCount.incrementAndGet();
    }
    public void setGlobalCount(Integer value){
        this.globalCount.set(value);
    }

    public void clearAllValues() {
        lock.lock(); // Блокировка для обеспечения потокобезопасности
        try {
            for (String key : complexMap.keySet()) {
                complexMap.put(key, new ArrayList<>()); // Заменяем значение каждого ключа пустым списком списков
            }
        } finally {
            lock.unlock(); // Снимаем блокировку
        }
    }

    public void addNewListByKey(String key, List<String> newList) {
        lock.lock(); // Блокировка для обеспечения потокобезопасности
        try {
            // Получаем список списков по ключу. Если его нет, создаем новый список списков.
            List<List<String>> listOfLists = complexMap.computeIfAbsent(key, k -> new ArrayList<>());

            // Добавляем новый список в список списков
            listOfLists.add(0, newList);
        } finally {
            lock.unlock(); // Снимаем блокировку
        }
    }
    public void addNewListByKeyLast(String key, List<String> newList) {
        lock.lock(); // Блокировка для обеспечения потокобезопасности
        try {
            // Получаем список списков по ключу. Если его нет, создаем новый список списков.
            List<List<String>> listOfLists = complexMap.computeIfAbsent(key, k -> new ArrayList<>());

            // Добавляем новый список в список списков
            listOfLists.add(newList);
        } finally {
            lock.unlock(); // Снимаем блокировку
        }
    }


    // Метод для добавления элемента
    public void put(String key, List<List<String>> value) {
        lock.lock(); // Блокировка для записи
        try {
            complexMap.put(key, value);
        } finally {
            lock.unlock(); // Снимаем блокировку
        }
    }

    // Метод для получения элемента по ключу
    public List<List<String>> get(String key) {
        return complexMap.get(key); // Чтение без блокировки, так как ConcurrentHashMap уже потокобезопасна
    }

    // Метод для изменения элемента
    public void update(String key, List<List<String>> value) {
        lock.lock(); // Блокировка для записи
        try {
            complexMap.replace(key, value);
        } finally {
            lock.unlock(); // Снимаем блокировку
        }
    }

    // Добавление нового списка к существующему ключу
    public void addToList(String key, List<String> newList) {
        lock.lock(); // Блокировка для записи
        try {
            complexMap.computeIfAbsent(key, k -> new ArrayList<>()).add(newList);
        } finally {
            lock.unlock(); // Снимаем блокировку
        }
    }
    public void removeLastFromList(String key) {
        lock.lock(); // Блокировка для безопасного доступа к структуре
        try {
            if (complexMap.containsKey(key) && !complexMap.get(key).isEmpty()) {
                List<List<String>> lists = complexMap.get(key);
                if (!lists.isEmpty()) {
                    List<String> lastList = lists.get(lists.size() - 1);
                    if (!lastList.isEmpty()) {
                        lastList.remove(lastList.size() - 1); // Удаляем последний элемент из последнего списка
                    }
                    // Если последний список стал пустым, удаляем и его
                    if (lastList.isEmpty()) {
                        lists.remove(lists.size() - 1);
                    }
                }
            }
        } finally {
            lock.unlock(); // Снимаем блокировку
        }
    }


}
