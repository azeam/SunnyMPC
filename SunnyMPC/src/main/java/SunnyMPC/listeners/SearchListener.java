
package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JTable;
import javax.swing.SwingWorker;

import com.google.gson.Gson;

import org.jdesktop.swingx.JXTree;

import SunnyMPC.Communicate;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Helper;
import SunnyMPC.Track;
import SunnyMPC.TrackBuilder;
import SunnyMPC.TreeBuilder;

public class SearchListener implements ActionListener {
    JXTree tree;
    JTable table;
    GUIBuilder gui = new GUIBuilder();

    public SearchListener(JXTree tree, JTable table) {
        this.tree = tree;
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String searchString = arg0.getActionCommand().toString();
        if (searchString.length() > 0) {
            getServerData(searchString);
        }
        else {
            // if empty search string, get all artists
            TreeBuilder treeBuilder = new TreeBuilder(table);
            treeBuilder.getServerData();
        }
    }

    // TODO: skip all of this and just do a local filtering of the jtree...
    private void getServerData(String searchString) {
        SwingWorker<List<String>, List<String>> sw = new SwingWorker<List<String>, List<String>>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                Helper helper = new Helper();
                Communicate com = new Communicate();
                // TODO: group by artist? or get all data and build full tree artist-album-track?
                List<String> resultList = com.sendCmd("search title " + helper.escapeString(searchString));

                // TODO: fix this very hacky way to split the list
                TrackBuilder builder = new TrackBuilder(resultList, "Track:");
                List<String> treatedData = builder.getTracks();
                HashSet<String> artists = new HashSet<String>();
                // get json data for each song
                Gson gson = new Gson();
                for (String s : treatedData) {
                    Track track = gson.fromJson(s, Track.class);
                    artists.add(track.getArtist());
                }
                List<String> lArtists = new ArrayList<String>();
                for (String a : artists) {
                    lArtists.add(a);
                }
                return lArtists;
            }

            @Override
            protected void done() {
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