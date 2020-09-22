package SunnyMPC.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import SunnyMPC.Communicate;
import SunnyMPC.GUIBuilder;

public class PlayTrackListener implements ListSelectionListener {
    JTable table;
    private String mbalbumId = "";
    boolean run = true;

    public PlayTrackListener(JTable table) {
        this.table = table;
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // get value from hidden id column
        // check if row exists because removing column when updating data will trigger
        // valuechanged, causing IOB
        run = false;
        int i = table.getSelectedRow();
        if (i >= 0 && !arg0.getValueIsAdjusting()) {
            String id = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
            mbalbumId = table.getModel().getValueAt(table.getSelectedRow(), 1).toString();

            Communicate.sendCmd("playid " + id);
            String path = mbalbumId + ".jpg";
            File checkFile = new File(path);
            if (mbalbumId.length() > 0 && !checkFile.exists()) {
                getCover("https://coverartarchive.org/release/" + mbalbumId);
            }
            else if (mbalbumId.length() > 0 && checkFile.exists()) {
                GUIBuilder guiBuilder = new GUIBuilder();
                guiBuilder.showAlbumImage(path);
            }
            getTrackInfo(id);
        }
    }

    // TODO: check if song changes, then update cover
    private void getTrackInfo(String id) {
        SwingWorker<String, String> sw = new SwingWorker<String, String>() {
			@Override
			protected String doInBackground() throws Exception {
                Thread.sleep(1000); // this will ensure that run in not set to true while sleeping in while loop (causing it not to stop and several loops running when changing track)
                run = true;
                Communicate.connect();
                List<String> status = Communicate.getStatus("status");
                while (status.contains("state: play") && run) {
                    status = Communicate.getStatus("status");
                    System.out.println(status);
                    Thread.sleep(1000);
                }
                Communicate.disconnect();
                return id;
			}
        };
        sw.execute();
    }

    private String getCover(String url) {
        SwingWorker<String, String> sw = new SwingWorker<String, String>() {

            @Override
            protected String doInBackground() throws Exception {
                StringBuilder sb = null;
                BufferedReader br;
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setInstanceFollowRedirects(false);
                con.connect();
                con.getInputStream();

                if (con.getResponseCode() == 307 || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                        || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    String redirectUrl = con.getHeaderField("Location");
                    System.out.println(redirectUrl);
                    return getCover(redirectUrl);
                } else if (con.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    Gson gson = new Gson();
                    JsonObject obj = gson.fromJson(sb.toString(), JsonObject.class);
                    JsonArray images = (JsonArray) obj.get("images").getAsJsonArray();
                    JsonObject image = images.get(0).getAsJsonObject();
                    JsonObject thumbnails = image.get("thumbnails").getAsJsonObject();
                    String small = thumbnails.get("small").getAsString();

                    try (InputStream in = new URL(small).openStream()) {
                        String path = mbalbumId + ".jpg";
                        Files.copy(in, Paths.get(path));
                        return path;
                    }
                }
                return url;
            }

            @Override
            protected void done() {
                try {
                    String path = get();
                    if (path.equals(mbalbumId + ".jpg")) {
                        GUIBuilder guiBuilder = new GUIBuilder();
                        guiBuilder.showAlbumImage(path);
                    }
                } 
                catch (InterruptedException e) { 
                    e.printStackTrace(); 
                }  
                catch (ExecutionException e) { 
                    if (e.getMessage().startsWith("java.io.FileNotFoundException")) {
                        System.out.println("No cover available");
                    }
                    else {
                        e.printStackTrace();
                    } 
                } 
            } 
        };
    sw.execute();
    return url;
    } 
}