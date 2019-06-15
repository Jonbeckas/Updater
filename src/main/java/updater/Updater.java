package updater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
    }

    public void checkForUpdate() {
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
        URL url = new URL("https://api.github.com/repos/" + this.settings.getGithubuser() + "/" + this.settings.getRepository() + "/releases");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.connect();
        InputStream inStream = connection.getInputStream();
        String text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();
        String ver = "";
        try {
            JSONArray json = new JSONArray(text);
            JSONObject release = new JSONObject(json.get(0).toString());
            JSONArray assets = new JSONArray(release.get("assets").toString());
            JSONObject asset = new JSONObject(assets.get(0).toString());
            String[] vers = release.getString("tag_name").split("v");
            ver = vers[1];
            URL link = new URL(asset.getString("browser_download_url"));
            this.settings.getOnSucces().onSucces(ver,link);
        } catch (Exception e) {
            this.settings.getOnError().onError(ver,e);
        }
    }

    private void getReleasesString() throws IOException {
        URL url = new URL("https://api.github.com/repos/" + this.settings.getGithubuser() + "/" + this.settings.getRepository() + "/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.connect();
        InputStream inStream = connection.getInputStream();
        String text = new Scanner(inStream, "UTF-8").useDelimiter("\\Z").next();
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
    }

}
