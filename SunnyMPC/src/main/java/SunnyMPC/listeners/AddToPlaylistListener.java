package SunnyMPC.listeners;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import SunnyMPC.Communicate;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Helper;
import SunnyMPC.Track;

public class AddToPlaylistListener implements TreeSelectionListener {
    JTable tree;

    public AddToPlaylistListener(JTable tree) {
        this.tree = tree;
    }
    
	@Override
    public void valueChanged(TreeSelectionEvent arg0) {
        TreePath selectedPath = arg0.getPath();
        DefaultMutableTreeNode albumNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        DefaultMutableTreeNode artistNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(1);

        List<String> rowData = new ArrayList<String>();
        if (albumNode == null) {return;}
        
        if (albumNode.isLeaf()) {
            Helper helper = new Helper();
            String selectedAlbum = albumNode.toString();
            String selectedArtist = artistNode.toString();
            List<String> playlist = Communicate.sendCmd("command_list_begin\nclear\nsearchadd artist " + helper.escapeString(selectedArtist) + " album " + helper.escapeString(selectedAlbum) + "\nplaylistinfo\ncommand_list_end");
            Track track = null;
            for (String row : playlist) {
                if (row.startsWith("file:")) {
                    track = new Track();
                }
                else if (row.startsWith("Title:")) {
                    track.setTitle(row.substring(row.indexOf(" ") + 1));
                }
                else if (row.startsWith("Album:")) {
                    track.setAlbum(row.substring(row.indexOf(" ") + 1));
                }
                else if (row.startsWith("Artist:") || row.startsWith("AlbumArtist:")) {
                    track.setArtist(row.substring(row.indexOf(" ") + 1));
                }
                else if (row.startsWith("MUSICBRAINZ_ALBUMID:")) {
                    track.setMBAlbumId(row.substring(row.indexOf(" ") + 1));
                }
                else if (row.startsWith("MUSICBRAINZ_ALBUMARTISTID:")) {
                    track.setMBArtistId(row.substring(row.indexOf(" ") + 1));
                }
                else if (row.startsWith("duration:")) {
                    track.setDuration(Double.parseDouble(row.substring(row.indexOf(" ") + 1)));
                }
                else if (row.startsWith("Time:")) {
                    track.setTime(Integer.parseInt(row.substring(row.indexOf(" ") + 1)));
                }
                else if (row.startsWith("Id: ")) {
                    track.setId(Integer.parseInt(row.substring(row.indexOf(" ") + 1)));
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = "";
                    try {
                        jsonString = mapper.writeValueAsString(track);
                        rowData.add(jsonString);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                
            }
            GUIBuilder guiBuilder = new GUIBuilder();
            guiBuilder.setTableData(rowData);
        }

    }


}