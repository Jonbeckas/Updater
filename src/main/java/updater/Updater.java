package updater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

@SuppressWarnings("Duplicates")
public class Updater {
    private UpdaterSettings settings;
    public Updater(UpdaterSettings updaterSettings) {
        this.settings = updaterSettings;
        checkForUpdate();
    }

    private void checkForUpdate() {
        if (this.settings.isGetPreRelease()) {
            try {
                getPrereleaseString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                getReleasesString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPrereleaseString() throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.github.com/repos/" + this.settings.getGithubuser() + "/" + this.settings.getRepository() + "/releases");
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("User-Agent","PostmanRuntime/7.17.1");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if(responseCode != HttpURLConnection.HTTP_OK){
                System.out.println("ERROR: "+responseCode);
                return;
            }
            String server_response = readStream(connection.getInputStream());
            String text = server_response;
            String ver = "";
            try {
                JSONArray json = new JSONArray(text);
                JSONObject release = new JSONObject(json.get(0).toString());
                JSONArray assets = new JSONArray(release.get("assets").toString());
                JSONObject asset = new JSONObject(assets.get(0).toString());
                String[] vers = release.getString("tag_name").split("v");
                ver = vers[0];
                URL link = new URL(asset.getString("browser_download_url"));
                this.settings.getOnSucces().onSucces(ver,link);
            } catch (Exception e) {
                this.settings.getOnError().onError(ver,e);
            }
        } finally {
            connection.disconnect();
        }

    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private void getReleasesString() throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.github.com/repos/" + this.settings.getGithubuser() + "/" + this.settings.getRepository() + "/releases/latest");
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("User-Agent","PostmanRuntime/7.17.1");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if(responseCode != HttpURLConnection.HTTP_OK){
                System.out.println("ERROR: "+responseCode);
                return;
            }
            String text = readStream(connection.getInputStream());
            System.out.println(text);
            String ver = "";
            try {
                JSONObject json = new JSONObject(text);
                JSONArray assets = new JSONArray(json.get("assets").toString());
                JSONObject asset = new JSONObject(assets.get(0).toString());
                ver = json.getString("tag_name") + "";
                URL link = new URL(asset.getString("browser_download_url"));
                this.settings.getOnSucces().onSucces(ver,link);
            } catch (Exception e) {
                this.settings.getOnError().onError(ver, e);
            }
        } finally {
            connection.disconnect();
        }

    }

}
