package SunnyMPC.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
         getServerData();
      }
   }

   private void getServerData() {
      SwingWorker<Boolean, Boolean> sw = new SwingWorker<Boolean, Boolean>() {
         @Override
         protected Boolean doInBackground() throws Exception {
            Helper helper = new Helper();
            List<String> artistStringList = helper.cleanupList(Constants.listartists);
            System.out.println(artistStringList);
            gui.fillAlbumList(artistStringList);

            // display track info in case something is playing since before
            gui.setTrackText("");
            if (artistStringList.size() > 0) {
               return false;
            } else {
               return true;
            }
         }

         @Override
         protected void done() {
            try {
               if (get()) {
                  // only get track info if artistlist has finished downloading or there will be interference with the commands
                  // TODO: this would be better prevented if properly setting up a non static socket
                  TrackInfo.getTrackInfo();
               }
            } catch (InterruptedException e) {
               e.printStackTrace();
            } catch (ExecutionException e) {
               e.printStackTrace();
            }
               DisplayTable.displayTable();
            }
         };
         sw.execute();
      }
}