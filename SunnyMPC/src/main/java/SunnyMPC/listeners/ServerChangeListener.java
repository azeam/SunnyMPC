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
   GUIBuilder gui = new GUIBuilder();

   @Override
   public void itemStateChanged(ItemEvent arg0) {
      // get and display info from server when changing server
      if (arg0.getStateChange() == ItemEvent.SELECTED) {
         TrackInfo.run = false;
         Communicate.ip = arg0.getItem().toString();
         gui.showAlbumImage(Constants.noImage);
         gui.setTrackText("");
         getServerData();
      }
   }

   private void getServerData() {
      SwingWorker<Boolean, Boolean> sw = new SwingWorker<Boolean, Boolean>() {
         @Override
         protected Boolean doInBackground() throws Exception {
            // prevent some interference between commands by sleeping a little while before getting new data...
            // TODO: this would likely be better prevented if properly setting up a non static socket
            Thread.sleep(Constants.sleeptime);
            Helper helper = new Helper();
            List<String> artistStringList = helper.cleanupList(Constants.listartists);
            gui.fillAlbumList(artistStringList);
            return true;            
         }

         @Override
         protected void done() {
               TrackInfo.getTrackInfo();
               DisplayTable.displayTable();
            }
         };
         sw.execute();
      }
}