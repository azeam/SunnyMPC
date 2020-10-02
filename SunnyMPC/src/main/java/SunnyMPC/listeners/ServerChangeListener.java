package SunnyMPC.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTable;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.GUIBuilder;
import SunnyMPC.TrackInfo;
import SunnyMPC.TreeBuilder;

public class ServerChangeListener implements ItemListener {
   GUIBuilder gui = new GUIBuilder();
   JTable table;
   TreeBuilder treeBuilder = new TreeBuilder(table);

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
         
         // get all artists
         treeBuilder.getServerData();
      }
   }

   
}