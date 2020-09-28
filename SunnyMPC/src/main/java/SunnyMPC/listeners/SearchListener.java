
package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.google.gson.Gson;

import org.jdesktop.swingx.JXTree;

import SunnyMPC.Communicate;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Helper;
import SunnyMPC.Track;
import SunnyMPC.TrackBuilder;

public class SearchListener implements ActionListener {
    JXTree tree;

    public SearchListener(JXTree tree) {
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String searchString = arg0.getActionCommand().toString();
        getServerData(searchString);
    }

    private void getServerData(String searchString) {
        SwingWorker<List<String>, List<String>> sw = new SwingWorker<List<String>, List<String>>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                Helper helper = new Helper();
                Communicate com = new Communicate();
                // TODO: group by artist? or get all data and build full tree artist-album-track?
                List<String> resultList = com.sendCmd("search any " + helper.escapeString(searchString));

                TrackBuilder builder = new TrackBuilder(resultList);
                List<String> treatedData = builder.getTracks();
                
                // get json data for each song
                Gson gson = new Gson();
                for (String s : treatedData) {
                    // TODO: does not build because last row is not Id: but Track:
                    Track track = gson.fromJson(s, Track.class);
                    System.out.println(track.getArtist());
                }
                return resultList;
            }

            @Override
            protected void done() {
                GUIBuilder gui = new GUIBuilder();
                try {
                    gui.fillAlbumList(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
              }
           };
           sw.execute();
        }

    
}