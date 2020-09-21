package apple.questing.sheets;

import apple.questing.data.Quest;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SheetsQuery {
    public static Collection<Quest> allQuests = new ArrayList<>();

    public static List<Integer> update() throws IOException {
        List<Integer> fails = new ArrayList<>();
        ValueRange questValueRange = SheetsConstants.sheets.get(SheetsConstants.spreadsheetId, SheetsRanges.ALL_QUESTS).execute();
        List<List<Object>> questValues = questValueRange.getValues();
        int i = 0;
        for (List<Object> quest : questValues) {
            try {
                String name = quest.get(0).toString();
                String reqsString = quest.get(1).toString();
                String[] reqs = reqsString.isEmpty() ? new String[0] : reqsString.split(",");
                short level = Short.parseShort(quest.get(2).toString());
                Quest.Length length = Quest.Length.valueOf(quest.get(3).toString().toUpperCase());
                double time = Double.parseDouble(quest.get(4).toString());
                double collectionTime = Double.parseDouble(quest.get(5).toString());
                int xp = Integer.parseInt(quest.get(6).toString());
                int emerald = Integer.parseInt(quest.get(7).toString());
                allQuests.add(new Quest(name, reqs, level, length, time, collectionTime, xp, emerald));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                fails.add(i);
            }
            i++;
        }
        return fails;
    }
}
