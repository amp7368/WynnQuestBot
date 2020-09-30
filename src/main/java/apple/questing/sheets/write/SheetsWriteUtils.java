package apple.questing.sheets.write;

import com.google.api.services.sheets.v4.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SheetsWriteUtils {
    @NotNull
    public static List<RowData> convertToRowData(List<List<String>> data) {
        List<RowData> rowDatas = new ArrayList<>();
        for (List<String> rowIter : data) {
            RowData rowData = new RowData();
            List<CellData> cells = new ArrayList<>();
            for (String cellIter : rowIter) {
                cells.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(cellIter)));
            }
            rowData.setValues(cells);
            rowDatas.add(rowData);
        }
        return rowDatas;
    }

    public static void setRowFormat(RowData rowData, boolean bold, Color color) {
        if (color == null)
            for (CellData cell : rowData.getValues()) {
                cell.setUserEnteredFormat(new CellFormat().setTextFormat(new TextFormat().setBold(bold)).setBackgroundColor(color));
            }
        else
            for (CellData cell : rowData.getValues()) {
                cell.setUserEnteredFormat(new CellFormat().setTextFormat(new TextFormat().setBold(bold)));
            }
    }

    public static Color makeColor(float a, float r, float g, float b) {
        return new Color().setAlpha(a / 255).setRed(r / 255).setGreen(g / 255).setBlue(b / 255);
    }

    public static List<List<String>> switchMajorDimension(List<List<String>> oldData) {
        List<List<String>> newData = new ArrayList<>();
        int newMaxLength = 0;
        int newMaxSubLength = oldData.size();
        for (List<String> oldSub : oldData) {
            newMaxLength = Math.max(newMaxLength, oldSub.size());
        }
        for (int i = 0; i < newMaxLength; i++) {
            newData.add(new ArrayList<>());
            for (int j = 0; j < newMaxSubLength; j++)
                newData.get(i).add(null);
        }
        for (int i = 0; i < newMaxSubLength; i++) {
            final List<String> oldDataSub = oldData.get(i);
            final int oldDataSubSize = oldDataSub.size();
            for (int j = 0; j < oldDataSubSize; j++) {
                newData.get(j).set(i, oldDataSub.get(j));
            }
        }
        return trimNull(newData);
    }

    private static List<List<String>> trimNull(List<List<String>> oldData) {
        List<List<String>> newData = new ArrayList<>();
        for (List<String> oldSub : oldData) {
            int end = 0;
            for (int i = oldSub.size() - 1; i != -1; i--) {
                if (oldSub.get(i) != null) {
                    end = i + 1;
                    break;
                }
            }
            List<String> newSub = new ArrayList<>();
            int i = 0;
            for (String oldCell : oldSub) {
                if (i++ == end)
                    break;
                newSub.add(oldCell);
            }
            newData.add(newSub);
        }
        return newData;
    }
}
