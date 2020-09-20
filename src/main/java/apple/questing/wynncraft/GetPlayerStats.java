package apple.questing.wynncraft;


import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetPlayerStats {
    private static final String BASE_URL = "https://api.wynncraft.com/v2/";

    public static JSONObject get(String name) throws IOException {
        InputStream is = new URL(BASE_URL + String.format("player/%s/stats", name)).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String jsonText = readAll(rd);
        JSONObject json = new JSONObject(jsonText);
        is.close();
        return json;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
