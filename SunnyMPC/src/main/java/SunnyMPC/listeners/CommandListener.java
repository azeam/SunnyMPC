package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.DisplayTable;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Track;
import SunnyMPC.TrackBuilder;
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
                    List<String> current = Communicate.sendCmd(Constants.currentSong);
                    TrackBuilder builder = new TrackBuilder(current);
                    Track track = builder.getTrack();
                    String mbalbumId = track.getMbalbumId();
                    TrackInfo.run = false;
                    TrackInfo trackInfo = new TrackInfo(mbalbumId);
                    String path = mbalbumId + ".jpg";
                    File checkFile = new File(path);
                    if (mbalbumId.length() > 0 && !checkFile.exists()) {
                        trackInfo.getCover(Constants.coverBaseUrl + mbalbumId);
                    }
                    else if (mbalbumId.length() > 0 && checkFile.exists()) {
                        guiBuilder.showAlbumImage(path);
                    }
                    TrackInfo.getTrackInfo(track);
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