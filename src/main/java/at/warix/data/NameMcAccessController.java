package at.warix.data;

import at.warix.exceptions.VoteException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static java.lang.Boolean.parseBoolean;

public class NameMcAccessController {
    private String URL_ALL_USERS = "https://api.namemc.com/server/maficraft.de/likes";
    private String URL_HAS_USER_VOTED = "https://api.namemc.com/server/maficraft.de/likes?profile=%s";

    private static NameMcAccessController instance;

    private NameMcAccessController() {
    }

    public static NameMcAccessController getInstance() {
        if (instance == null) {
            instance = new NameMcAccessController();
        }
        return instance;
    }

    public boolean verifyVote(UUID uuid) throws IOException, VoteException {

        URL url = new URL(String.format(URL_HAS_USER_VOTED, uuid.toString()));
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new VoteException("There was a problem while accessing the REST endpoint: " + conn.getResponseMessage());
        } else {
            StringBuilder sb = new StringBuilder();
            try (InputStreamReader isr = new InputStreamReader((conn.getInputStream()))) {

                try (BufferedReader br = new BufferedReader(isr)) {
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                }
            }
            return Boolean.parseBoolean(sb.toString());
        }
    }
}
