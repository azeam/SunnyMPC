package SunnyMPC.listeners;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.TrackInfo;

public class PlayTrackListener implements ListSelectionListener {
    JTable table;

    public PlayTrackListener(JTable table) {
        this.table = table;
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // get value from hidden id column
        // check if row exists because removing column when updating data will trigger
        // valuechanged, causing IOB
        int i = table.getSelectedRow();
        if (i >= 0 && !arg0.getValueIsAdjusting()) {
            String id = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
            Communicate com = new Communicate();
            com.sendCmd(Constants.playId + id);
            // stop the track info fetcher
            TrackInfo.run = false;
            TrackInfo.getTrackInfo();
        }
    }

    
}