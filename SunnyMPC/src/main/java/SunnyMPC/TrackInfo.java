package SunnyMPC;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TrackInfo {
    public static boolean run;
    public static String mbalbumId;

    public static void getTrackInfo() {
        Helper helper = new Helper();

        SwingWorker<Integer, String> sw = new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                Thread.sleep(Constants.sleeptime * 2); // this will ensure that run is not set to true while sleeping in while loop
                                    // (causing it not to stop and several loops running when changing track)
                run = true;
                Communicate.connect();
                List<String> status = Communicate.getStatus(Constants.status);
                List<String> current = Communicate.getStatus(Constants.currentSong);
                List<String> checkCurrent = new ArrayList<String>();

                TrackBuilder builder = new TrackBuilder(current);
                Track track = builder.getTrack();
                mbalbumId = track.getMbalbumId();
                String path = mbalbumId + ".jpg";
                File checkFile = new File(path);
                if (mbalbumId.length() > 0 && !checkFile.exists()) {
                    getCover(Constants.coverBaseUrl + mbalbumId);
                } else if (mbalbumId.length() > 0 && checkFile.exists()) {
                    GUIBuilder guiBuilder = new GUIBuilder();
                    guiBuilder.showAlbumImage(path);
                }

                while (status.contains("state: play") && run) {
                    checkCurrent = Communicate.getStatus(Constants.currentSong);
                    // if song ends and new song starts, build new track object and restart worker
                    // to get new info
                    if (!current.equals(checkCurrent)) {
                        builder = new TrackBuilder(checkCurrent);
                        track = builder.getTrack();
                        getTrackInfo();
                        run = false;
                    } else {
                        // TODO: clean up and check for invalid values
                        status = Communicate.getStatus(Constants.status);
                        String elapsed = "";
                        String audio = "";
                        String bitrate = "";
                        for (String row : status) {
                            if (row.startsWith("elapsed:")) {
                                Double dElapsed = Double.parseDouble(row.substring(row.indexOf(" ") + 1));
                                int iElapsed = (int) Math.floor(dElapsed);
                                elapsed = helper.getMinutes(iElapsed);
                            } else if (row.startsWith("audio:")) {
                                audio = row.substring(row.indexOf(" ") + 1);
                            } else if (row.startsWith("bitrate:")) {
                                bitrate = row.substring(row.indexOf(" ") + 1);
                            }
                        }

                        // need to call invokeAndWait to avoid flickering when updating text, the strings below are 
                        // required to be re-set to make them final for the run method
                        String[] aAudio = audio.split(":");
                        GUIBuilder guiBuilder = new GUIBuilder();
                        String artist = track.getArtist();
                        String title = track.getTitle();
                        String el = elapsed;
                        String dur = helper.getMinutes(track.getTime());
                        String bit = bitrate;
                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {
                                public void run() {
                                    guiBuilder.setTrackText("<html><center><font size=5 color=white><b>" + 
                                    title + "</b><br>" + artist + "<br><br></font><font size=4 color=white>" + 
                                    el + " of " + dur + "<br><br>" + 
                                    aAudio[0] + " Hz " + aAudio[1] + " bit " + aAudio[2] + " channels. Bitrate " + bit
                                    + " kbps</font></center></html>");
                                }
                            });
                        } catch (InvocationTargetException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        

                        
                    }
                    Thread.sleep(Constants.sleeptime);
                }
                Communicate.disconnect();
                return 0;
            }
        };
        sw.execute();
    }

    protected static String getCover(String url) {
        SwingWorker<String, String> sw = new SwingWorker<String, String>() {

            @Override
            protected String doInBackground() throws Exception {
                StringBuilder sb = null;
                BufferedReader br;
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setInstanceFollowRedirects(false);
                con.connect();
                con.getInputStream();

                // follow redirects in the json response automatically by restarting worker on 30* response
                if (con.getResponseCode() == 307 || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                        || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    String redirectUrl = con.getHeaderField("Location");
                    return getCover(redirectUrl);
                } 
                // if valid json in the redirect is found, parse it, download the image and display it
                else if (con.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    Gson gson = new Gson();
                    JsonObject obj = gson.fromJson(sb.toString(), JsonObject.class);
                    
                    // get the url for the 250px image
                    JsonArray images = (JsonArray) obj.get("images").getAsJsonArray();
                    String small = images.get(0).getAsJsonObject().get("thumbnails").getAsJsonObject().get("small").getAsString();

                    // download and save image
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