package SunnyMPC.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JTable;
import javax.swing.SwingWorker;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.DisplayTable;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Helper;
import SunnyMPC.TrackInfo;

public class ServerChangeListener implements ItemListener {
   GUIBuilder gui = new GUIBuilder();
   JTable table;

   public ServerChangeListener(JTable table) {
      this.table = table;
   }

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
      SwingWorker<List<String>, Boolean> sw = new SwingWorker<List<String>, Boolean>() {
         @Override
         protected List<String> doInBackground() throws Exception {
            Helper helper = new Helper();
            List<String> artistStringList = helper.cleanupList(Constants.listartists);
            return artistStringList;
         }

         @Override
         protected void done() {
            try {
               gui.fillAlbumList(get());
            } catch (InterruptedException | ExecutionException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
               TrackInfo.getTrackInfo(table);
               DisplayTable.displayTable();
            }
         };
         sw.execute();
      }
}