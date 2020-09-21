package apple.questing.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class WynncraftClass {
    public Integer level;
    public Collection<String> questsCompleted = new ArrayList<>();

    public WynncraftClass(JSONObject classJson) {
        level = (Integer) ((JSONObject)((JSONObject)classJson.get("professions")).get("combat")).get("level");
        for (Object questCompleted : (JSONArray) ((JSONObject) classJson.get("quests")).get("list")) {
            questsCompleted.add(questCompleted.toString());
        }
    }
}
