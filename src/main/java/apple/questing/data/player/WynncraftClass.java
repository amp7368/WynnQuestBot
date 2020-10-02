package apple.questing.data.player;

import apple.questing.data.quest.Quest;
import apple.questing.sheets.SheetsQuery;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

public class WynncraftClass {
    public Integer combatLevel;
    public String name;
    public String namePretty;
    public int totalLevel;
    public int dungeonsWon;
    public Collection<String> questsCompleted = new ArrayList<>();
    public Collection<Quest> questsNotCompleted = new ArrayList<>();


    public WynncraftClass(JSONObject classJson) {
        combatLevel = (Integer) ((JSONObject) ((JSONObject) classJson.get("professions")).get("combat")).get("level");
        name = classJson.getString("name");
        char[] nameP = name.toCharArray();
        nameP[0] = Character.toUpperCase(nameP[0]);
        StringBuilder pretty = new StringBuilder();
        for (int i = 0; i < nameP.length; i++) {
            if (!Character.isAlphabetic(nameP[i]) || nameP[i] == ' ')
                break;
            else
                pretty.append(nameP[i]);
        }
        namePretty = pretty.toString();


        totalLevel = classJson.getInt("level");
        dungeonsWon = (classJson.getJSONObject("dungeons").getInt("completed"));

        for (Object questCompleted : (JSONArray) ((JSONObject) classJson.get("quests")).get("list")) {
            questsCompleted.add(questCompleted.toString());
        }
        SheetsQuery.allQuests.forEach(quest -> {
            if (!questsCompleted.contains(quest.name)) questsNotCompleted.add(quest);
        });
    }
}
