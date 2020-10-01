package apple.questing.data.player;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WynncraftPlayer {
    public List<WynncraftClass> classes = new ArrayList<>();
    public String name;

    public WynncraftPlayer(JSONArray classesJson, String name) {
        this.name = name;
        for (Object classObject : classesJson) {
            JSONObject classJson = (JSONObject) classObject;
            classes.add(new WynncraftClass(classJson));
        }
    }

    public WynncraftPlayer(List<WynncraftClass> classes, String name) {
        this.classes = classes;
        this.name = name;
    }
}
