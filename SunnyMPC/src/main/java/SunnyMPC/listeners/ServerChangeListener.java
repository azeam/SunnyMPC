package SunnyMPC.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import SunnyMPC.Communicate;
import SunnyMPC.GUIBuilder;

public class ServerChangeListener implements ItemListener {
   @Override
   public void itemStateChanged(ItemEvent arg0) {
      if (arg0.getStateChange() == ItemEvent.SELECTED) {
         Object server = arg0.getItem();
         Communicate.ip=server.toString();
         GUIBuilder gui = new GUIBuilder();
         gui.fillAlbumList();
      }
   }

   
}