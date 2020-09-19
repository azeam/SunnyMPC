package SunnyMPC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;

public class UpdateListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ae) {
        Communicate.sendCmd("clear");
        Communicate.sendCmd("add Adele");

        String playlist = Communicate.getPlaylist("playlistinfo");
        List<Integer> ids = new ArrayList<Integer>();
        List<String> titles = new ArrayList<String>();
        String[] track = playlist.split("file:");
        for (String row : track) {
            if (row.contains("Id: ") && row.contains("Title: ")) {
                ids.add(Integer.parseInt(row.split("Id: ")[1].trim()));
                String title = row.split("Title: ")[1];
                titles.add(title.split("Track: ")[0].trim());
            }
        }
        // split playlistinfo and build table data from that, including pos for song position to start that
        // when double clicking row, set listener and start that
        // split on file: do for loop and add row with id, title etc.
        // TODO: list of data and column names to add

        Communicate.sendCmd("pause");
        GUIBuilder guiBuilder = new GUIBuilder();
        Integer[] idData = ids.toArray(new Integer[0]);
        String[] titleData = titles.toArray(new String[0]);
        guiBuilder.setTableData("ID", idData, "Title", titleData);
    }

    
}