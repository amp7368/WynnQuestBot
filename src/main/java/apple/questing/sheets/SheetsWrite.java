package apple.questing.sheets;

import apple.questing.QuestMain;
import apple.questing.data.FinalQuestOptionsAll;
import apple.questing.data.WynncraftClass;
import apple.questing.data.reaction.ClassChoiceMessage;
import com.google.api.services.sheets.v4.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

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
            String sheetId = addSheet(discordId);
            writeData(questOptions, wynncraftClass, classChoiceMessage, sheetId);
        } catch (IOException | ParseException e) {
            e.printStackTrace();// todo deal with error
        }
    }

    private static void writeData(FinalQuestOptionsAll questOptions, WynncraftClass wynncraftClass, ClassChoiceMessage classChoiceMessage, String sheetId) {

    }

    @NotNull
    private static String addSheet(long discordId) throws IOException, ParseException {
        synchronized (syncObject) {
            String id = getSheetId(discordId);
            if (id != null)
                return id;
            // otherwise make the sheet
            Spreadsheet spreadsheet = new Spreadsheet().setProperties(
                    new SpreadsheetProperties().set("title", "Quests (Wynncraft)")
            ).setSheets(Arrays.asList(
                    new Sheet().setProperties(
                            new SheetProperties().setTitle("Overview")
                    ), new Sheet().setProperties(
                            new SheetProperties().setTitle("% | Emerald")
                    ), new Sheet().setProperties(
                            new SheetProperties().setTitle("% | Xp")
                    ), new Sheet().setProperties(
                            new SheetProperties().setTitle("Amount | Emerald")
                    ), new Sheet().setProperties(
                            new SheetProperties().setTitle("Amount | Xp")
                    ), new Sheet().setProperties(
                            new SheetProperties().setTitle("Time | Emerald")
                    ), new Sheet().setProperties(
                            new SheetProperties().setTitle("Time | Xp")
                    )
                    )
            );
            Spreadsheet spreadSheet = QuestMain.service.spreadsheets().create(spreadsheet).execute();
            final String spreadsheetId = spreadSheet.getSpreadsheetId();
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
