package SunnyMPC.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.google.gson.Gson;

import SunnyMPC.Communicate;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Helper;
import SunnyMPC.Track;
import SunnyMPC.TrackBuilder;

public class AddToPlaylistListener implements TreeSelectionListener {
    JTable tree;

    public AddToPlaylistListener(JTable tree) {
        this.tree = tree;
    }
    
	@Override
    public void valueChanged(TreeSelectionEvent arg0) {
        startThread(arg0);
    }

    private static void startThread(TreeSelectionEvent arg0) { 
        SwingWorker<List<Object[]>, List<Object[]>> sw = new SwingWorker<List<Object[]>, List<Object[]>>() { 

            @Override
            protected List<Object[]> doInBackground() throws Exception {
                Helper helper = new Helper();

                TreePath selectedPath = arg0.getPath();
                DefaultMutableTreeNode albumNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                DefaultMutableTreeNode artistNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(1);

                List<String> rowData = new ArrayList<String>();
                if (albumNode == null) {return null;}
                
                if (albumNode.isLeaf()) {
                    String selectedAlbum = albumNode.toString();
                    String selectedArtist = artistNode.toString();
                    List<String> playlist = Communicate.sendCmd("command_list_begin\nclear\nfindadd artist " + helper.escapeString(selectedArtist) + " album " + helper.escapeString(selectedAlbum) + "\nplaylistinfo\ncommand_list_end");
                    TrackBuilder builder = new TrackBuilder(playlist);
                    rowData = builder.getTracks();
                }
                Integer[] ids = new Integer[rowData.size()];
                String[] titles = new String[rowData.size()];
                String[] artists = new String[rowData.size()];
                String[] albums = new String[rowData.size()];
                String[] times = new String[rowData.size()];
                String[] mbAlbums = new String[rowData.size()];

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