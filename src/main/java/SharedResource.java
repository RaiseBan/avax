import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SharedResource {
    private CopyOnWriteArrayList<List<Float>> sharedList = new CopyOnWriteArrayList<>();

    public SharedResource(List<List<Float>> list){
        Collections.copy(sharedList, list);
    }
    public void addValue(float value, String color) {
//        if color == "green"
//        sharedList.add(value);
    }

//    public List<Float> getValues() {
////        return sharedList;
//    }
}