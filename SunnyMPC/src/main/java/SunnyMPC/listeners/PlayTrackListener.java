package SunnyMPC.listeners;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.event.MouseInputListener;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.TrackInfo;

public class PlayTrackListener implements MouseInputListener {
    JTable table;

    public PlayTrackListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        int selected = table.getSelectedRow();
        int realSelected = table.convertRowIndexToModel(selected); // needed to get real value after sorting list
        if (selected >= 0 && arg0.getClickCount() == 2) {
            String id = table.getModel().getValueAt(realSelected, 0).toString();
            Communicate com = new Communicate();
            com.sendCmd(Constants.playId + id);
            // stop the track info fetcher
            TrackInfo.run = false;
            TrackInfo.getTrackInfo(table);
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mousePressed(MouseEvent arg0) {}

    @Override
    public void mouseReleased(MouseEvent arg0) {}

    @Override
    public void mouseDragged(MouseEvent arg0) {}

    @Override
    public void mouseMoved(MouseEvent arg0) {}

    
}