package apple.questing.sheets.write;

import apple.questing.data.FinalQuestOptions;
import apple.questing.data.Quest;
import apple.questing.data.WynncraftClass;
import apple.questing.data.combo.FinalQuestCombo;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.utils.Pretty;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.*;

import static apple.questing.sheets.SheetsRanges.*;
import static apple.questing.sheets.write.SheetsWriteUtils.makeColor;
import static apple.questing.sheets.SheetsConstants.BANDS_PER_SHEET;

public class SheetsWriteData {

    public static final Color FIRST_BAND_COLOR = makeColor(255f, 255f, 255f, 255f);
    public static final Color SECOND_BAND_COLOR = makeColor(255f, 231, 249, 239);
    public static final Color HEADER_COLOR = makeColor(255f, 99, 210, 151);

    public static List<Request> write(FinalQuestOptions questOptions, WynncraftClass wynncraftClass, ClassChoiceMessage classChoiceMessage, String spreadSheetId, SheetName sheetName, int order) throws IOException {
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
            final List<Quest> quests = questCombo.getQuests();

            List<String> row = new ArrayList<>();
            if (questCombo == null) {
                row.add("");
                row.add("Enter more arguments to get this page");
                continue;
            }
            row.add("Collection Included");
            row.add("Xp Desired");
            row.add("Quest Count");
            row.add("Reward");
            row.add("Time");
            row.add("Reward/Time");
            data.add(row);

            row = new ArrayList<>();
            row.add(String.valueOf(questCombo.isIncludeCollection));
            row.add(String.valueOf(questCombo.isXpDesired));
            row.add(String.valueOf(quests.size()));
            row.add(questCombo.getAmountPretty());
            row.add(questCombo.getTimePretty());
            row.add(questCombo.getAmountPerTimePretty());
            data.add(row);
            data.add(Collections.emptyList());

            row = new ArrayList<>();
            row.add("Quest Name");
            row.add("Total Time");
            row.add("Time");
            row.add("Collection Time");
            row.add(questCombo.isXpDesired ? "Xp" : "Emerald");
            row.add("Level");
            row.add("Requirements");
            data.add(row);

            for (Quest quest : quests) {
                row = new ArrayList<>();
                row.add(quest.name);
                row.add((quest.time + quest.collectionTime) + " mins");
                row.add(quest.time + " mins");
                row.add(quest.collectionTime + " mins");
                if (questCombo.isXpDesired) row.add(Pretty.commas(quest.xp));
                else row.add(Pretty.getMon(quest.emerald));
                row.add(Pretty.commas(quest.levelMinimum));
                row.add(String.join(", ", quest.allRequirements));
                data.add(row);
            }
            data.add(Collections.emptyList());
            data.add(Collections.emptyList());
        }

        List<RowData> rows = SheetsWriteUtils.convertToRowData(data);
        int i = 0;
        for (FinalQuestCombo finalQuestCombo : questOptions.getList()) {
            SheetsWriteUtils.setRowFormat(rows.get(i++), false, HEADER_COLOR);
            SheetsWriteUtils.setRowFormat(rows.get(i), true, HEADER_COLOR);
            SheetsWriteUtils.setRowFormat(rows.get(i + 2), true, null);
            i += finalQuestCombo.getQuests().size() + 5;
        }
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setUpdateCells(new UpdateCellsRequest().setFields("*").setRows(rows).
                setRange(new GridRange().setSheetId(sheetId).setStartColumnIndex(0).setStartRowIndex(0).setEndRowIndex(rows.size()))));

        int row = 0;
        int finalQuestComboI = order * BANDS_PER_SHEET;
        for (FinalQuestCombo finalQuestCombo : questOptions.getList()) {
            final int size = finalQuestCombo.getQuests().size();
            requests.add(new Request().setAddBanding(new AddBandingRequest().setBandedRange(new BandedRange().setBandedRangeId(finalQuestComboI++).
                    setRange(new GridRange().setSheetId(sheetId).setStartColumnIndex(0).setEndColumnIndex(7).setStartRowIndex(row + 3).setEndRowIndex(row + size + 4)).
                    setRowProperties(new BandingProperties().
                            setHeaderColor(HEADER_COLOR).
                            setFirstBandColor(FIRST_BAND_COLOR).
                            setSecondBandColor(SECOND_BAND_COLOR)
                    ))));
            row += size + 6;
        }
        return requests;
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
