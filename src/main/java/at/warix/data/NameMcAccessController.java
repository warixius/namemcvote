package at.warix.data;

import at.warix.exceptions.HTTPException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;

public class NameMcAccessController {
    private final String URL_ALL_USERS = "https://api.namemc.com/server/%s/likes";
    private final String URL_HAS_USER_VOTED = "https://api.namemc.com/server/%s/likes?profile=%s";
    private final String linkToNameMcVotePage = "https://namemc.com/server/%s";

    private String serverToVoteFor;

    private static NameMcAccessController instance;

    private NameMcAccessController() {
    }

    public static NameMcAccessController getInstance() {
        if (instance == null) {
            instance = new NameMcAccessController();
        }
        return instance;
    }

    public String getServerToVoteFor() {
        return serverToVoteFor;
    }

    public void setServerToVoteFor(String serverToVoteFor) {
        this.serverToVoteFor = serverToVoteFor;
    }

    public String getUrlAllUsers() {
        return String.format(URL_ALL_USERS, serverToVoteFor);
    }

    public String getLinkToNameMcVotePage() {
        return String.format(linkToNameMcVotePage, serverToVoteFor);
    }

    public boolean verifyVote(UUID uuid) throws IOException, HTTPException {

        URL url = new URL(String.format(URL_HAS_USER_VOTED, serverToVoteFor, uuid.toString()));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new HTTPException(String.valueOf(conn.getResponseCode()));
        } else {
            String sb = readRequestBody(conn.getInputStream());
            return Boolean.parseBoolean(sb);
        }
    }

    private String readRequestBody(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader((stream))) {
            try (BufferedReader br = new BufferedReader(isr)) {
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
            }
        }
        return sb.toString();
    }
}
