package SunnyMPC.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import SunnyMPC.Communicate;
import SunnyMPC.DisplayTable;
import SunnyMPC.GUIBuilder;
import SunnyMPC.Helper;

public class ServerChangeListener implements ItemListener {
   @Override
   public void itemStateChanged(ItemEvent arg0) {
      // get and display info from server when changing server
      if (arg0.getStateChange() == ItemEvent.SELECTED) {
         Object server = arg0.getItem();
         Communicate.ip=server.toString();
         GUIBuilder gui = new GUIBuilder();
         Helper helper = new Helper();
         List<String> artistStringList = helper.cleanupList("list albumartist");
         gui.fillAlbumList(artistStringList);
         DisplayTable.displayTable();
      }
   }   
}