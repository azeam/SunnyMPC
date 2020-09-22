package SunnyMPC.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.SwingWorker;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.DisplayTable;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Helper;
import SunnyMPC.TrackInfo;

public class ServerChangeListener implements ItemListener {
   @Override
   public void itemStateChanged(ItemEvent arg0) {
      // get and display info from server when changing server
      if (arg0.getStateChange() == ItemEvent.SELECTED) {
         Object server = arg0.getItem();
         Communicate.ip = server.toString();
         getServerData();
      }
   }   

   private void getServerData() {
         SwingWorker<Boolean, Boolean> sw = new SwingWorker<Boolean, Boolean>() { 
            @Override
            protected Boolean doInBackground() throws Exception { 
               GUIBuilder gui = new GUIBuilder();
               Helper helper = new Helper();
               List<String> artistStringList = helper.cleanupList(Constants.listartists);
               gui.fillAlbumList(artistStringList);

               // display track info in case something is playing since before
               gui.setTrackText("");
               TrackInfo.run = false;
               TrackInfo.getTrackInfo();   
               return true;
            }

            @Override
            protected void done() { 
               DisplayTable.displayTable();
            }
         };
         sw.execute();
      }
}