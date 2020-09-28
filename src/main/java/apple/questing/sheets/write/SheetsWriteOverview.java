package apple.questing.sheets.write;

import apple.questing.data.FinalQuestOptions;
import apple.questing.data.FinalQuestOptionsAll;
import apple.questing.data.WynncraftClass;
import apple.questing.data.reaction.ClassChoiceMessage;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static apple.questing.sheets.SheetsRanges.OVERVIEW_SHEET_ID;

public class SheetsWriteOverview {
    public static Request writeOverview(FinalQuestOptionsAll questOptions, WynncraftClass wynncraftClass, ClassChoiceMessage classChoiceMessage, String sheetId) throws IOException {
        List<List<String>> data = new ArrayList<>();

        // add the xpIsWanted vs xpIsNotWanted row
        List<String> row = new ArrayList<>();
        row.add("");
        row.add("");
        row.add("xp is not wanted");
        row.add("xp is not wanted");
        row.add("xp is wanted");
        row.add("xp is wanted");
        data.add(row);

        // add the include collection vs exclude collection
        row = new ArrayList<>();
        row.add("");
        row.add("");
        row.add("include collection");
        row.add("include collection");
        row.add("exclude collection");
        row.add("exclude collection");
        data.add(row);
        int i = 0;
        for (FinalQuestOptions answer : questOptions.getList()) {
            switch (i++) {
                case 0:
                    row = new ArrayList<>();
                    row.add("% | Amount/Time");
                    data.add(row);
                    break;
                case 1:
                    row = new ArrayList<>();
                    row.add("% | Time");
                    data.add(row);
                    break;
                case 2:
                    row = new ArrayList<>();
                    row.add("Amount | Amount/Time");
                    data.add(row);
                    break;
                case 3:
                    row = new ArrayList<>();
                    row.add("Amount | Time");
                    data.add(row);
                    break;
                case 4:
                    row = new ArrayList<>();
                    row.add("Time | Amount/Time");
                    data.add(row);
                    break;
                case 5:
                    row = new ArrayList<>();
                    row.add("Time | Amount");
                    data.add(row);
                    break;
            }

            row = new ArrayList<>();
            row.add("");
            row.add("Quest Count");
            row.add(String.valueOf(answer.cnx.getQuests().size()));
            row.add(String.valueOf(answer.ncnx.getQuests().size()));
            row.add(String.valueOf(answer.cx.getQuests().size()));
            row.add(String.valueOf(answer.ncx.getQuests().size()));
            data.add(row);

            row = new ArrayList<>();
            row.add("");
            row.add("Reward");
            row.add(String.valueOf(answer.cnx.getAmountPretty()));
            row.add(String.valueOf(answer.ncnx.getAmountPretty()));
            row.add(String.valueOf(answer.cx.getAmountPretty()));
            row.add(String.valueOf(answer.ncx.getAmountPretty()));
            data.add(row);

            row = new ArrayList<>();
            row.add("");
            row.add("Time");
            row.add(String.valueOf(answer.cnx.getTimePretty()));
            row.add(String.valueOf(answer.ncnx.getTimePretty()));
            row.add(String.valueOf(answer.cx.getTimePretty()));
            row.add(String.valueOf(answer.ncx.getTimePretty()));
            data.add(row);

            row = new ArrayList<>();
            row.add("");
            row.add("Reward/Time");
            row.add(String.valueOf(answer.cnx.getAmountPerTimePretty()));
            row.add(String.valueOf(answer.ncnx.getAmountPerTimePretty()));
            row.add(String.valueOf(answer.cx.getAmountPerTimePretty()));
            row.add(String.valueOf(answer.ncx.getAmountPerTimePretty()));
            data.add(row);
        }
        return new Request().setUpdateCells(new UpdateCellsRequest().setFields("*").setRows(SheetsWriteUtils.convertToRowData(data)).
                setRange(new GridRange().setSheetId(OVERVIEW_SHEET_ID).setStartColumnIndex(0).setEndColumnIndex(6).setStartRowIndex(0).setEndRowIndex(32)));
    }

}
