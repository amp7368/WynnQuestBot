package apple.questing;

import apple.questing.discord.DiscordBot;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


public class QuestMain {
    static final String APPLICATION_NAME = "CreditBot";
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String DRIVE_TOKENS_DIRECTORY_PATH = "driveTokens";
    private static final String SHEETS_TOKENS_DIRECTORY_PATH = "sheetsTokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> DRIVE_SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final List<String> SHEETS_SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String DRIVE_CREDENTIALS_FILE_PATH = "/driveCredentials.json";
    private static final String SHEETS_CREDENTIALS_FILE_PATH = "/sheetsCredentials.json";
    public static Sheets serviceSheets;
    public static Drive serviceDrive;

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the sheetsCredentials.json file cannot be found.
     */
    static Credential getSheetsCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = apple.questing.QuestMain.class.getResourceAsStream(SHEETS_CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + SHEETS_CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SHEETS_SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(SHEETS_TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        final Credential user = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        in.close();
        return user;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the sheetsCredentials.json file cannot be found.
     */
    private static Credential getDriveCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = QuestMain.class.getResourceAsStream(DRIVE_CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + DRIVE_CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, DRIVE_SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(DRIVE_TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        serviceSheets = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getSheetsCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        serviceDrive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getDriveCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        DiscordBot bot = new DiscordBot();
        try {
            bot.enableDiscord();
        } catch (LoginException e) {
            System.err.println("The bot has not logged in!");
        }
        System.out.println("Started QuestBot");
    }
}
