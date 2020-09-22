package SunnyMPC;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TrackInfo {
    public static boolean run;
    private String mbalbumId;

    public TrackInfo(String mbalbumId) {
        this.mbalbumId = mbalbumId;
    }

    public static void getTrackInfo(Track track) {
        SwingWorker<Integer, String> sw = new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                Thread.sleep(1000); // this will ensure that run in not set to true while sleeping in while loop
                                    // (causing it not to stop and several loops running when changing track)
                run = true;
                int id = track.getId();
                Communicate.connect();
                List<String> status = Communicate.getStatus("status");
                System.out.println("status" + status);
                System.out.println("run" + run);

                while (status.contains("state: play") && run) {
                    status = Communicate.getStatus("status");
                    System.out.println("Current track id" + id);
                    System.out.println("Current track title" + track.getTitle());
                    for (String row : status) {
                        if (row.startsWith("elapsed:")) {
                            System.out.println((row.substring(row.indexOf(" ") + 1)));
                        }
                        else if (row.startsWith("duration:")) {
                            System.out.println((row.substring(row.indexOf(" ") + 1)));
                        }
                        else if (row.startsWith("audio:")) {
                            System.out.println((row.substring(row.indexOf(" ") + 1)));
                        }
                    }
                    Thread.sleep(1000);
                }
                System.out.println("Disconnecting in TrackInfo");
                Communicate.disconnect();
                return id;
			}
        };
        sw.execute();
    }

    public String getCover(String url) {
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