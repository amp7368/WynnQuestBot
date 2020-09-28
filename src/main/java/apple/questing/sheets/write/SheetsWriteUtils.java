package apple.questing.sheets.write;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.RowData;
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
}
