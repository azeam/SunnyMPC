package SunnyMPC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ae) {
        Communicate.sendCmd("clear");
        Communicate.sendCmd("add Adele");

        String playlist = Communicate.getPlaylist("playlistinfo");
        
        String[] track = playlist.split("file:");
        for (String row : track) {
            System.out.println(row);
        }
        // split playlistinfo and build table data from that, including pos for song position to start that
        // when double clicking row, set listener and start that
        // split on file: do for loop and add row with id, title etc.

        Communicate.sendCmd("pause");
        GUIBuilder guiBuilder = new GUIBuilder();
        guiBuilder.setTableData(track);
    }
    
}