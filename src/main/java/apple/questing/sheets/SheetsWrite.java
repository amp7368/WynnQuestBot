package apple.questing.sheets;

import apple.questing.QuestMain;
import apple.questing.data.FinalQuestOptionsAll;
import apple.questing.data.WynncraftClass;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static apple.questing.QuestMain.serviceDrive;
import static apple.questing.QuestMain.serviceSheets;

public class SheetsWrite {
    private static final String SHEET_IDS_FILE_PATH;
    private static final JSONParser PARSER = new JSONParser();
    private static final Object syncObject = new Object();

    static {
        List<String> list = Arrays.asList(QuestMain.class.getProtectionDomain().getCodeSource().getLocation().getPath().split("/"));
        SHEET_IDS_FILE_PATH = String.join("/", list.subList(0, list.size() - 1)) + "/data/discordIdToSheet.data";
    }

    public static void writeSheet(FinalQuestOptionsAll questOptions, WynncraftClass wynncraftClass, ClassChoiceMessage classChoiceMessage, long discordId) {
        try {
            String sheetId = tryAddSheet(discordId);
            writeData(questOptions, wynncraftClass, classChoiceMessage, sheetId);
        } catch (IOException | ParseException e) {
            e.printStackTrace();// todo deal with error
        }
    }

    private static void writeData(FinalQuestOptionsAll questOptions, WynncraftClass wynncraftClass, ClassChoiceMessage classChoiceMessage, String sheetId) throws IOException {
        List<Request> requests = new ArrayList<>();
        requests.add(SheetsWriteOverview.writeOverview(questOptions, wynncraftClass, classChoiceMessage, sheetId));
        requests.add(SheetsWriteData.write(questOptions.answerPercAPT, wynncraftClass, classChoiceMessage, sheetId, SheetsWriteData.SheetName.PERC_APT));
        requests.add(SheetsWriteData.write(questOptions.answerPercTime, wynncraftClass, classChoiceMessage, sheetId, SheetsWriteData.SheetName.PERC_TIME));
        requests.add(SheetsWriteData.write(questOptions.answerAmountAPT, wynncraftClass, classChoiceMessage, sheetId, SheetsWriteData.SheetName.AMOUNT_APT));
        requests.add(SheetsWriteData.write(questOptions.answerAmountTime, wynncraftClass, classChoiceMessage, sheetId, SheetsWriteData.SheetName.AMOUNT_TIME));
        requests.add(SheetsWriteData.write(questOptions.answerTimeAPT, wynncraftClass, classChoiceMessage, sheetId, SheetsWriteData.SheetName.TIME_APT));
        requests.add(SheetsWriteData.write(questOptions.answerTimeAmount, wynncraftClass, classChoiceMessage, sheetId, SheetsWriteData.SheetName.TIME_AMOUNT));

        serviceSheets.spreadsheets().batchUpdate(sheetId, new BatchUpdateSpreadsheetRequest().setRequests(requests)).execute();

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
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("% | Emerald").setSheetId(1)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("% | Xp").setSheetId(2)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("Amount | Emerald").setSheetId(3)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("Amount | Xp").setSheetId(4)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("Time | Emerald").setSheetId(5)
                    ), new Sheet().setProperties(
                            new SheetProperties().setGridProperties(new GridProperties().setColumnCount(200)).setTitle("Time | Xp").setSheetId(6)
                    )
                    )
            );
            Spreadsheet spreadSheet = serviceSheets.spreadsheets().create(spreadsheet).execute();
            final String spreadsheetId = spreadSheet.getSpreadsheetId();
            serviceDrive.permissions().create(spreadsheetId, new Permission().setRole("reader").setType("anyone")).execute();
            System.out.println(spreadsheetId);
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
