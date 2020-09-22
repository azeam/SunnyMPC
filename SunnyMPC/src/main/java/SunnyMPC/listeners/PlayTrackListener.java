package SunnyMPC.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import SunnyMPC.Communicate;

public class PlayTrackListener implements ListSelectionListener {
    JTable table;
    private String mbalbumId = "";

    public PlayTrackListener(JTable table) {
        this.table = table;
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // get value from hidden id column
        // check if row exists because removing column when updating data will trigger
        // valuechanged, causing IOB
        int i = table.getSelectedRow();
        if (i >= 0 && !arg0.getValueIsAdjusting()) {
            Communicate.sendCmd("playid " + table.getModel().getValueAt(table.getSelectedRow(), 0).toString());
            mbalbumId = table.getModel().getValueAt(table.getSelectedRow(), 1).toString();
            String path = mbalbumId + ".jpg";
            File checkFile = new File(path);
            if (mbalbumId.length() > 0 && !checkFile.exists()) {
                getCover("https://coverartarchive.org/release/" + mbalbumId);
            }
            else if (mbalbumId.length() > 0 && checkFile.exists()) {
                updateCover(path);
            }
        // TODO: start another worker that updates play time
        }

    }

    private void updateCover(String path) {
        // TODO: update cover...
        System.out.println("update cover");
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
                    String large = thumbnails.get("large").getAsString();

                    try (InputStream in = new URL(large).openStream()) {
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
                        updateCover(path);
                    }
                }  
                catch (InterruptedException e) { 
                    e.printStackTrace(); 
                }  
                catch (ExecutionException e) { 
                    e.printStackTrace(); 
                } 
            } 
        };
    sw.execute();
    return url;
    } 
}