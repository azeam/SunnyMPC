package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingWorker;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.DisplayTable;
import SunnyMPC.GUIBuilder;
import SunnyMPC.TrackInfo;

public class CommandListener implements ActionListener {
    private String cmd;
    GUIBuilder guiBuilder = new GUIBuilder();
            
    public CommandListener(String cmd) {
        this.cmd = cmd;
	}

	@Override
    public void actionPerformed(ActionEvent ae) {
        sendCmd();
    }

    private void sendCmd() {
        SwingWorker<Boolean, Boolean> sw = new SwingWorker<Boolean, Boolean>() { 
            @Override
            protected Boolean doInBackground() throws Exception { 
                Communicate.sendCmd(cmd);
                
                // update track data when going back/forward and send it to the track info handler for display
                if (cmd.equals(Constants.next) || cmd.equals(Constants.previous)) {
                    TrackInfo.run = false;
                    TrackInfo.getTrackInfo();
                }
                else if (cmd.equals(Constants.clear)) {
                    DisplayTable.displayTable();
                }
                else if (cmd.equals(Constants.stop)) {
                    guiBuilder.setTrackText("");
                }
                return true;
            }
        };
        sw.execute();
    }
}