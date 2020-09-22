package SunnyMPC.listeners;

import java.io.File;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Track;
import SunnyMPC.TrackBuilder;
import SunnyMPC.TrackInfo;

public class PlayTrackListener implements ListSelectionListener {
    JTable table;

    public PlayTrackListener(JTable table) {
        this.table = table;
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // stop the track info fetcher
        TrackInfo.run = false;
        // get value from hidden id column
        // check if row exists because removing column when updating data will trigger
        // valuechanged, causing IOB
        int i = table.getSelectedRow();
        if (i >= 0 && !arg0.getValueIsAdjusting()) {

            String id = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
            String mbalbumId = table.getModel().getValueAt(table.getSelectedRow(), 1).toString();
            TrackInfo trackInfo = new TrackInfo(mbalbumId);
            Communicate.sendCmd(Constants.playId + id);

            // get album id and check if cover exists, otherwise start download attempt
            String path = mbalbumId + ".jpg";
            File checkFile = new File(path);
            if (mbalbumId.length() > 0 && !checkFile.exists()) {
                trackInfo.getCover(Constants.coverBaseUrl + mbalbumId);
            }
            else if (mbalbumId.length() > 0 && checkFile.exists()) {
                GUIBuilder guiBuilder = new GUIBuilder();
                guiBuilder.showAlbumImage(path);
            }

            // build track object and pass to the info fetcher
            List<String> current = Communicate.sendCmd("currentsong");
            TrackBuilder builder = new TrackBuilder(current);
            Track track = builder.getTrack();
            TrackInfo.getTrackInfo(track);
        }
    }

    
}