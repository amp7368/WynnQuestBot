package apple.questing.data;

import apple.questing.sheets.SheetsQuery;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class WynncraftClass {
    public Integer level;
    public String name;
    public Collection<String> questsCompleted = new ArrayList<>();
    public Collection<Quest> questsNotCompleted = new ArrayList<>();

    public WynncraftClass(JSONObject classJson) {
        level = (Integer) ((JSONObject) ((JSONObject) classJson.get("professions")).get("combat")).get("level");
        name = classJson.getString("name");
        for (Object questCompleted : (JSONArray) ((JSONObject) classJson.get("quests")).get("list")) {
            questsCompleted.add(questCompleted.toString());
        }
        SheetsQuery.allQuests.forEach(quest -> {
            if (questsCompleted.contains(quest.name)) questsNotCompleted.add(quest);
        });
    }
}
