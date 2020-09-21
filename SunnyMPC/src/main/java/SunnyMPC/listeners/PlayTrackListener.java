package SunnyMPC.listeners;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import SunnyMPC.Communicate;

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
        if (i >= 0) {
            Communicate.sendCmd("playid " + table.getModel().getValueAt(table.getSelectedRow(), 0).toString());
        }
    }
}