package apple.questing.sheets.write;

import apple.questing.data.answer.FinalQuestOptions;
import apple.questing.data.answer.FinalQuestCombo;
import apple.questing.data.quest.QuestLinked;
import apple.questing.utils.Pretty;
import com.google.api.services.sheets.v4.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static apple.questing.sheets.SheetsRanges.*;
import static apple.questing.sheets.write.SheetsWriteUtils.convertToRowData;
import static apple.questing.sheets.write.SheetsWriteUtils.makeColor;
import static apple.questing.sheets.SheetsConstants.BANDS_PER_SHEET;

public class SheetsWriteData {

    public static final Color FIRST_BAND_COLOR = makeColor(255f, 255f, 255f, 255f);
    public static final Color SECOND_BAND_COLOR = makeColor(255f, 231, 249, 239);
    public static final Color HEADER_COLOR = makeColor(255f, 99, 210, 151);

    public static List<Request> write(FinalQuestOptions questOptions, SheetName sheetName, boolean isAllClasses) {
        Integer sheetId = sheetName.getSheetId();

        List<List<String>> data = new ArrayList<>();
        if (questOptions == null) {
            List<String> row = new ArrayList<>();
            row.add("Enter more arguments to get this page");
            data.add(row);
            @NotNull List<RowData> rows = convertToRowData(data);
            List<Request> requests = new ArrayList<>();
            requests.add(new Request().setUpdateCells(new UpdateCellsRequest().setFields("*").setRows(rows).
                    setRange(new GridRange().setSheetId(sheetId).setStartColumnIndex(0).setStartRowIndex(0).setEndRowIndex(rows.size()))));
            int finalQuestComboI = (sheetName.getSheetId()) * BANDS_PER_SHEET;
            for (int i = 0; i < BANDS_PER_SHEET; i++) {
                requests.add(new Request().setAddBanding(new AddBandingRequest().setBandedRange(new BandedRange().setBandedRangeId(finalQuestComboI++).
                        setRange(new GridRange().setSheetId(sheetId).setStartColumnIndex(0).setEndColumnIndex(1).setStartRowIndex(i).setEndRowIndex(i + 1)).
                        setRowProperties(new BandingProperties().
                                setHeaderColor(HEADER_COLOR).
                                setFirstBandColor(FIRST_BAND_COLOR).
                                setSecondBandColor(SECOND_BAND_COLOR)
                        ))));
            }
            return requests;
        }
        for (FinalQuestCombo questCombo : questOptions.getList()) {
            List<String> row = new ArrayList<>();
            row.add("Collection Included");
            row.add("Xp Desired");
            row.add("Quest Count");
            row.add("Reward");
            row.add("Time");
            row.add("Reward/Time");
            data.add(row);

            row = new ArrayList<>();

            if (questCombo == null) {
                row.add("???");
                row.add("???");
                row.add("???");
                row.add("???");
                row.add("???");
                row.add("???");
                data.add(row);
                data.add(Collections.emptyList());
                row = new ArrayList<>();
                row.add("Quest Name");
                row.add("Total Time");
                row.add("Time");
                row.add("Collection Time");
                row.add("Xp | Emerald");
                row.add("Level");
                if (isAllClasses) {
                    row.add("Class");
                    row.add("Class' Level");
                    row.add("Class' Dungeons");
                }
                row.add("Requirements");
                data.add(row);
                data.add(Collections.emptyList());
                data.add(Collections.emptyList());
                continue;
            }

            final List<QuestLinked> quests = questCombo.getQuests();
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
            if (isAllClasses) {
                row.add("Class");
                row.add("Class' Level");
                row.add("Class' Dungeons");
            }
            row.add("Requirements");
            data.add(row);

            for (QuestLinked quest : quests) {
                row = new ArrayList<>();
                row.add(quest.name);
                row.add((quest.time + quest.collectionTime) + " mins");
                row.add(quest.time + " mins");
                row.add(quest.collectionTime + " mins");
                if (questCombo.isXpDesired) row.add(Pretty.commasXp(quest.xp));
                else row.add(Pretty.getMon(quest.emerald));
                row.add(Pretty.commasXp(quest.levelMinimum));
                if (isAllClasses) {
                    row.add(quest.playerClass.namePretty);
                    row.add(quest.playerClass.combatLevel + "/" + quest.playerClass.totalLevel);
                    row.add(String.valueOf(quest.playerClass.dungeonsWon));
                }
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
            if (finalQuestCombo == null)
                i += 5;
            else
                i += finalQuestCombo.getQuests().size() + 5;
        }
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setUpdateCells(new UpdateCellsRequest().setFields("*").setRows(rows).
                setRange(new GridRange().setSheetId(sheetId).setStartColumnIndex(0).setEndColumnIndex(10).setStartRowIndex(0).setEndRowIndex(rows.size()))));

        int row = 0;
        int finalQuestComboI = (sheetName.getSheetId()) * BANDS_PER_SHEET;
        for (FinalQuestCombo finalQuestCombo : questOptions.getList()) {
            int size;
            if (finalQuestCombo == null) {
                size = 0;
            } else
                size = finalQuestCombo.getQuests().size();
            requests.add(new Request().setAddBanding(new AddBandingRequest().setBandedRange(new BandedRange().setBandedRangeId(finalQuestComboI++).
                    setRange(new GridRange().setSheetId(sheetId).setStartColumnIndex(0).setEndColumnIndex(10).setStartRowIndex(row + 3).setEndRowIndex(row + size + 4)).
                    setRowProperties(new BandingProperties().
                            setHeaderColor(HEADER_COLOR).
                            setFirstBandColor(FIRST_BAND_COLOR).
                            setSecondBandColor(SECOND_BAND_COLOR)
                    ))));
            row += size + 6;
        }
        return requests;
    }

}
