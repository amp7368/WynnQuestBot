package apple.questing.data.player;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WynncraftPlayer {
    public List<WynncraftClass> classes = new ArrayList<>();

    public WynncraftPlayer(JSONArray classesJson) {
        for (Object classObject : classesJson) {
            JSONObject classJson = (JSONObject) classObject;
            classes.add(new WynncraftClass(classJson));
        }
    }
}
