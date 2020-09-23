package SunnyMPC;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.google.gson.Gson;

public class DisplayTable {
     public static void displayTable() {
        SwingWorker<List<Object[]>, List<Object[]>> sw = new SwingWorker<List<Object[]>, List<Object[]>>() { 
            // build track object for each track that is added to the playlist for display in the table
            // this takes a short while so using a worker in a new thread
            // some tracks may not have all information and jtable accepts [], 
            // this is why the object is built in this rather complicated way
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                Helper helper = new Helper();
                List<String> rowData = new ArrayList<String>();
                List<String> playlist = Communicate.sendCmd(Constants.playlistInfo);
                TrackBuilder builder = new TrackBuilder(playlist);
                rowData = builder.getTracks();
                
                Integer[] ids = new Integer[rowData.size()];
                String[] titles = new String[rowData.size()];
                String[] artists = new String[rowData.size()];
                String[] albums = new String[rowData.size()];
                String[] times = new String[rowData.size()];
                String[] mbAlbums = new String[rowData.size()];

                // re-build java object from json data
                Gson gson = new Gson();
                int i = 0;
                for (String s : rowData) {    
                    Track track = gson.fromJson(s, Track.class);
                    ids[i] = track.getId();
                    titles[i] = track.getTitle();
                    artists[i] = track.getArtist();
                    albums[i] = track.getAlbum();
                    times[i] = helper.getMinutes(track.getTime());
                    mbAlbums[i] = track.getMbalbumId();
                    i++;
                }

                // make list with arrays instead of passing every array
                List<Object[]> data = new ArrayList<Object[]>();
                data.add(ids);
                data.add(mbAlbums);
                data.add(titles);
                data.add(albums);
                data.add(artists);
                data.add(times);
                return data; 
            }

            @Override
            protected void done() { 
                try { 
                    List<Object[]> rowData = get();
                    if (rowData.size() > 0) {
                        // pass array list to builder for table display
                        GUIBuilder guiBuilder = new GUIBuilder();
                        guiBuilder.setTableData(rowData);
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
    } 
}