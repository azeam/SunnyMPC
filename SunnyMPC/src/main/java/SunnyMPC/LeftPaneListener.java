package SunnyMPC;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LeftPaneListener implements TreeSelectionListener {

    JTable tree;

    public LeftPaneListener(JTable tree) {
        this.tree = tree;
	}

	@Override
    public void valueChanged(TreeSelectionEvent arg0) {
        TreePath selectedPath = arg0.getPath();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        List<String> rowData = new ArrayList<String>();
        if (selectedNode == null) {return;}

        if (selectedNode.isLeaf()) {
            System.out.println("isleaf");
            String selected = selectedNode.toString();
            List<String> playlist = Communicate.sendCmd("command_list_begin\nclear\nsearchadd (Artist == Adele)\nplaylistinfo\ncommand_list_end");
                                                                 

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
       
        }

        GUIBuilder guiBuilder = new GUIBuilder();
        guiBuilder.setTableData(rowData);

    }


}