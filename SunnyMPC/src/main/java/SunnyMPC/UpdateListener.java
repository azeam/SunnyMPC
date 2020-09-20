package SunnyMPC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ae) {
        List<String> playlist = Communicate.sendCmd("command_list_begin\nclear\nadd Adele\nadd Aerosmith\nplaylistinfo\npause\ncommand_list_end");
        
        List<String> rowData = new ArrayList<String>();
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