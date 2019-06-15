package test;

import updater.Updater;
import updater.UpdaterSettings;

import java.net.URL;

public class Main {
    public static void main(String[] args) {
        new Main();
    }
    private Main() {
        UpdaterSettings settings = new UpdaterSettings("LowLevelSubmarine","Tronic",this::onError,this::onSucces,false);
        Updater updater = new Updater(settings);
    }

    private void onSucces(String s, URL url) {
        System.out.println(s);
        System.out.println(url);
    }

    private void onError(String s, Exception e) {
        e.printStackTrace();
    }
}
