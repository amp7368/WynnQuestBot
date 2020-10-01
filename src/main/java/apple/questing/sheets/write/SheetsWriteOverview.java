package apple.questing.sheets.write;

import apple.questing.data.answer.FinalQuestOptions;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.sheets.SheetsRanges;
import com.google.api.services.sheets.v4.model.*;

import java.util.ArrayList;
import java.util.List;


public class SheetsWriteOverview {
    public static Request writeOverview(FinalQuestOptionsAll questOptions) {
        List<List<String>> data = new ArrayList<>();

        // add the xpIsWanted vs xpIsNotWanted row
        List<String> row = new ArrayList<>();
        row.add("");
        row.add("");
        row.add("emeralds wanted");
        row.add("emeralds wanted");
        row.add("xp is wanted");
        row.add("xp is wanted");
        data.add(row);

        // add the include collection vs exclude collection
        row = new ArrayList<>();
        row.add("");
        row.add("");
        row.add("include collection");
        row.add("exclude collection");
        row.add("include collection");
        row.add("exclude collection");
        data.add(row);
        int i = 0;
        for (FinalQuestOptions answer : questOptions.getList()) {
            switch (i++) {
                case 0:
                    row = new ArrayList<>();
                    row.add("% | Amount/Time");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("This is calculated by default regardless of -t and -e arguments");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("The goal of this calculation is to reach 50% of rewards possible and maximize Reward/Time");
                    data.add(row);
                    break;
                case 1:
                    row = new ArrayList<>();
                    row.add("% | Time");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("This is calculated by default regardless of -t and -e arguments");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("The goal of this calculation is to reach 50% of rewards possible and minimize the time");
                    data.add(row);
                    break;
                case 2:
                    row = new ArrayList<>();
                    row.add("Amount | Amount/Time");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("This is calculated if the -e argument is provided");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("The goal of this calculation is to reach the reward given and maximize Reward/Time");
                    data.add(row);
                    break;
                case 3:
                    row = new ArrayList<>();
                    row.add("Amount | Time");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("This is calculated if the -e argument is provided");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("The goal of this calculation is to reach the reward given and minimize Time");
                    data.add(row);
                    break;
                case 4:
                    row = new ArrayList<>();
                    row.add("Time | Amount/Time");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("This is calculated if the -t argument is provided");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("The goal of this calculation is to maximize Amount/Time while Time is below the provided limit");
                    data.add(row);
                    break;
                case 5:
                    row = new ArrayList<>();
                    row.add("Time | Amount");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("This is calculated if the -t argument is provided");
                    data.add(row);
                    row = new ArrayList<>();
                    row.add("The goal of this calculation is to maximize Amount while Time is below the provided limit");
                    data.add(row);
                    break;
            }
            if (answer == null) {
                row.add("");
                row.add("Enter more arguments to get this page");
                continue;
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
        List<RowData> rows = SheetsWriteUtils.convertToRowData(data);
        SheetsWriteUtils.setColumnFormat(rows, 0, true, null);
        SheetsWriteUtils.setColumnFormat(rows, 1, true, null);
        return new Request().setUpdateCells(new UpdateCellsRequest().setFields("*").setRows(rows).
                setRange(new GridRange().setSheetId(SheetsRanges.SheetName.OVERVIEW_SHEET_ID.getSheetId()).setStartColumnIndex(0).setEndColumnIndex(6).setStartRowIndex(0).setEndRowIndex(45)));
    }

}
