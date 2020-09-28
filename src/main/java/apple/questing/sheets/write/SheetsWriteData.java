package apple.questing.sheets.write;

import apple.questing.data.FinalQuestOptions;
import apple.questing.data.FinalQuestOptionsAll;
import apple.questing.data.Quest;
import apple.questing.data.WynncraftClass;
import apple.questing.data.combo.FinalQuestCombo;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.utils.Pretty;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static apple.questing.sheets.SheetsRanges.*;

public class SheetsWriteData {
    public static Request write(FinalQuestOptions questOptions, WynncraftClass wynncraftClass, ClassChoiceMessage classChoiceMessage, String spreadSheetId, SheetName sheetName) throws IOException {
        int endColumnIndex = 0;
        Integer sheetId = -1;
        switch (sheetName) {
            case PERC_APT:
                sheetId = PERC_APT;
                break;
            case PERC_TIME:
                sheetId = PERC_TIME;
                break;
            case AMOUNT_APT:
                sheetId = AMOUNT_APT;
                break;
            case AMOUNT_TIME:
                sheetId = AMOUNT_TIME;
                break;
            case TIME_APT:
                sheetId = TIME_APT;
                break;
            case TIME_AMOUNT:
                sheetId = TIME_AMOUNT;
                break;
        }
        List<List<String>> data = new ArrayList<>();
        for (FinalQuestCombo questCombo : questOptions.getList()) {
            List<String> row = new ArrayList<>();
            row.add("Collection Included");
            row.add(String.valueOf(questCombo.isIncludeCollection));
            row.add("");
            row.add("Quest Name");
            final List<Quest> quests = questCombo.getQuests();
            for (Quest quest : quests) {
                row.add(quest.name);
            }
            data.add(row);

            row = new ArrayList<>();
            row.add("Xp Desired");
            row.add(String.valueOf(questCombo.isXpDesired));
            row.add("");
            row.add("Total Time");
            for (Quest quest : quests) {
                row.add(String.valueOf(quest.time + quest.collectionTime));
            }
            data.add(row);

            row = new ArrayList<>();
            final int questSize = quests.size();
            endColumnIndex = Math.max(questSize, endColumnIndex);
            row.add("Quest Count");
            row.add(String.valueOf(questSize));
            row.add("");
            row.add("Time");
            for (Quest quest : quests) {
                row.add(String.valueOf(quest.time));
            }
            data.add(row);

            row = new ArrayList<>();
            row.add("Reward");
            row.add(questCombo.getAmountPretty());
            row.add("");
            row.add("Collection Time");
            for (Quest quest : quests) {
                row.add(questCombo.isXpDesired ? Pretty.commas(quest.xp) : Pretty.getMon(quest.emerald));
            }
            data.add(row);

            row = new ArrayList<>();
            row.add("Time");
            row.add(questCombo.getAmountPretty());
            row.add("");
            row.add("Emerald");
            for (Quest quest : quests) {
                row.add(Pretty.getMon(quest.emerald));
            }
            data.add(row);

            row = new ArrayList<>();
            row.add("Reward/Time");
            row.add(questCombo.getAmountPretty());
            row.add("");
            row.add("Xp");
            for (Quest quest : quests) {
                row.add(Pretty.commas(quest.xp));
            }
            data.add(row);

            row = new ArrayList<>();
            row.add("");
            row.add("");
            row.add("");
            row.add("Level");
            for (Quest quest : quests) {
                row.add(Pretty.commas(quest.levelMinimum));
            }
            data.add(row);

            row = new ArrayList<>();
            row.add("");
            row.add("");
            row.add("");
            row.add("Requirements");
            for (Quest quest : quests) {
                row.add(String.join(", ", quest.allRequirements));
            }
            data.add(row);
            data.add(Collections.emptyList());
        }

        return new Request().setUpdateCells(new UpdateCellsRequest().setFields("*").setRows(SheetsWriteUtils.convertToRowData(data)).
                setRange(new GridRange().setSheetId(sheetId).setStartColumnIndex(0).setStartRowIndex(0).setEndRowIndex(36)));
    }

    public enum SheetName {
        PERC_APT,
        PERC_TIME,
        AMOUNT_APT,
        AMOUNT_TIME,
        TIME_APT,
        TIME_AMOUNT
    }
}
