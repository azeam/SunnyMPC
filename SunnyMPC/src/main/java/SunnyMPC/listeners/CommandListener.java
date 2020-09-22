package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import SunnyMPC.Communicate;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Track;
import SunnyMPC.TrackBuilder;
import SunnyMPC.TrackInfo;

public class CommandListener implements ActionListener {
    private String cmd;
    
    public CommandListener(String cmd) {
        this.cmd = cmd;
	}

	@Override
    public void actionPerformed(ActionEvent ae) {
        Communicate.sendCmd(cmd);
        if (cmd.equals("next")) {
            List<String> current = Communicate.sendCmd("currentsong");
            TrackBuilder builder = new TrackBuilder(current);
            Track track = builder.getTrack();
            String mbalbumId = track.getMbalbumId();
            TrackInfo.run = false;
            TrackInfo trackInfo = new TrackInfo(mbalbumId);
            String path = mbalbumId + ".jpg";
            File checkFile = new File(path);
            if (mbalbumId.length() > 0 && !checkFile.exists()) {
                trackInfo.getCover("https://coverartarchive.org/release/" + mbalbumId);
            }
            else if (mbalbumId.length() > 0 && checkFile.exists()) {
                GUIBuilder guiBuilder = new GUIBuilder();
                guiBuilder.showAlbumImage(path);
            }
            
            TrackInfo.getTrackInfo(track);
        }
    }
}