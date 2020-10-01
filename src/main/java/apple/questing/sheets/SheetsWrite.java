package apple.questing.sheets;

import apple.questing.QuestMain;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.sheets.write.SheetsWriteData;
import apple.questing.sheets.write.SheetsWriteOverview;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.Instant;
import java.util.*;

import static apple.questing.QuestMain.serviceDrive;
import static apple.questing.QuestMain.serviceSheets;
import static apple.questing.sheets.SheetsConstants.BANDS_PER_SHEET;
import static apple.questing.sheets.SheetsRanges.*;
import static apple.questing.sheets.write.SheetsWriteUtils.makeColor;

public class SheetsWrite {
    private static final String SHEET_IDS_FILE_PATH;
    private static final JSONParser PARSER = new JSONParser();
    private static final Object syncObject = new Object();

    static {
        List<String> list = Arrays.asList(QuestMain.class.getProtectionDomain().getCodeSource().getLocation().getPath().split("/"));
        SHEET_IDS_FILE_PATH = String.join("/", list.subList(0, list.size() - 1)) + "/data/discordIdToSheet.data";
    }

    public static String writeSheet(FinalQuestOptionsAll questOptions, WynncraftClass wynncraftClass, ClassChoiceMessage classChoiceMessage, long discordId) {
        try {
            String sheetId = tryAddSheet(discordId);
            writeData(questOptions, wynncraftClass, classChoiceMessage, sheetId);
            return sheetId;
        } catch (IOException | ParseException e) {
            e.printStackTrace();// todo deal with error
        }
        return null;
    }

    private static void writeData(FinalQuestOptionsAll questOptions, WynncraftClass wynncraftClass, ClassChoiceMessage classChoiceMessage, String spreadsheetId) throws IOException {
        List<Request> requests = new ArrayList<>();
        requests.add(SheetsWriteOverview.writeOverview(questOptions, wynncraftClass, classChoiceMessage, spreadsheetId));
        int order = 1;
        requests.addAll(SheetsWriteData.write(questOptions.answerPercAPT, wynncraftClass, classChoiceMessage, spreadsheetId, SheetsWriteData.SheetName.PERC_APT, order++));
        requests.addAll(SheetsWriteData.write(questOptions.answerPercTime, wynncraftClass, classChoiceMessage, spreadsheetId, SheetsWriteData.SheetName.PERC_TIME, order++));
        requests.addAll(SheetsWriteData.write(questOptions.answerAmountAPT, wynncraftClass, classChoiceMessage, spreadsheetId, SheetsWriteData.SheetName.AMOUNT_APT, order++));
        requests.addAll(SheetsWriteData.write(questOptions.answerAmountTime, wynncraftClass, classChoiceMessage, spreadsheetId, SheetsWriteData.SheetName.AMOUNT_TIME, order++));
        requests.addAll(SheetsWriteData.write(questOptions.answerTimeAPT, wynncraftClass, classChoiceMessage, spreadsheetId, SheetsWriteData.SheetName.TIME_APT, order++));
        requests.addAll(SheetsWriteData.write(questOptions.answerTimeAmount, wynncraftClass, classChoiceMessage, spreadsheetId, SheetsWriteData.SheetName.TIME_AMOUNT, order));
        List<Request> deleteRequests = new ArrayList<>();
        for (int band = BANDS_PER_SHEET; band < BANDS_PER_SHEET * 7; band++) // 6+1 because there are 6 sheets
            deleteRequests.add(new Request().setDeleteBanding(new DeleteBandingRequest().setBandedRangeId(band)));
        serviceSheets.spreadsheets().batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(deleteRequests)).execute();
        serviceSheets.spreadsheets().batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(requests)).execute();
    }

    @NotNull
    private static String tryAddSheet(long discordId) throws IOException, ParseException {
        synchronized (syncObject) {
            String id = getSheetId(discordId);
            if (id != null)
                return id;
            // otherwise make the sheet
            Spreadsheet spreadsheet = new Spreadsheet().setProperties(
                    new SpreadsheetProperties().set("title", "Quests (Wynncraft)")
            ).setSheets(Arrays.asList(
                    new Sheet().setProperties(
                            new SheetProperties().setTitle("Overview").setSheetId(0)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("% | Amount/Time").setSheetId(1)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("% | Time").setSheetId(2)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("Amount | Amount/Time").setSheetId(3)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("Amount | Time").setSheetId(4)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("Time | Amount/Time").setSheetId(5)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("Time | Amount").setSheetId(6)
                    )
                    )
            );
            Spreadsheet spreadSheet = serviceSheets.spreadsheets().create(spreadsheet).execute();
            final String spreadsheetId = spreadSheet.getSpreadsheetId();
            serviceDrive.permissions().create(spreadsheetId, new Permission().setRole("reader").setType("anyone")).execute();
            addBanding(spreadsheetId);
            JSONObject sheetToAdd = new JSONObject();
            sheetToAdd.put("discord", discordId);
            sheetToAdd.put("sheet", spreadsheetId);
            sheetToAdd.put("age", Instant.now().getEpochSecond());
            @NotNull JSONArray allIds = getSheetIds();
            allIds.add(sheetToAdd);
            BufferedWriter writer = new BufferedWriter(new FileWriter(getSheetIdsFile()));
            allIds.writeJSONString(writer);
            writer.close();
            return spreadsheetId;
        }
    }

    private static void addBanding(String spreadsheetId) throws IOException {
        List<Request> requests = new ArrayList<>();
        for (int i = 0, row = 2; i < 6; i++, row += 7) {
            requests.add(new Request().setAddBanding(new AddBandingRequest().setBandedRange(new BandedRange().setBandedRangeId(0).
                    setRange(new GridRange().setSheetId(OVERVIEW_SHEET_ID).setStartColumnIndex(0).setEndColumnIndex(6).setStartRowIndex(row).setEndRowIndex(row + 7)).
                    setRowProperties(new BandingProperties().
                            setHeaderColor(makeColor(255f, 99, 210, 151)).
                            setFirstBandColor(makeColor(255f, 255f, 255f, 255f)).
                            setSecondBandColor(makeColor(255f, 231, 249, 239))
                    ))));
        }

        for (int band = BANDS_PER_SHEET; band < BANDS_PER_SHEET * 7; band++) { // 6+1 because there are 6 sheets
            requests.add(new Request().setAddBanding(new AddBandingRequest().setBandedRange(new BandedRange().setBandedRangeId(band).
                    setRange(new GridRange().setSheetId(PERC_TIME).setStartColumnIndex(0).setEndColumnIndex(1).setStartRowIndex(band).setEndRowIndex(band + 1)).
                    setRowProperties(new BandingProperties().
                            setHeaderColor(makeColor(255f, 99, 210, 151)).
                            setFirstBandColor(makeColor(255f, 255f, 255f, 255f)).
                            setSecondBandColor(makeColor(255f, 231, 249, 239))))));
        }

        serviceSheets.spreadsheets().batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(requests)).execute();
    }

    @Nullable
    private static String getSheetId(long discordId) throws IOException, ParseException {
        JSONArray allIds = getSheetIds();

        for (Object allId : allIds) {
            if (((Long) ((JSONObject) allId).get("discord")) == discordId) {
                return ((JSONObject) allId).get("sheet").toString();
            }
        }
        return null;
    }

    @NotNull
    private static JSONArray getSheetIds() throws IOException, ParseException {
        File file = getSheetIdsFile();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Object allIdsObject = PARSER.parse(reader);
        if (!(allIdsObject instanceof JSONArray))
            throw new ParseException(1, allIdsObject);
        reader.close();
        JSONArray allIds = (JSONArray) allIdsObject;
        return allIds;
    }

    @NotNull
    private static File getSheetIdsFile() {
        File file = new File(SHEET_IDS_FILE_PATH);
        if (!file.exists()) {
            System.err.println("Please create file " + SHEET_IDS_FILE_PATH);
            System.exit(1);
            return null;
        }
        return file;
    }


}
