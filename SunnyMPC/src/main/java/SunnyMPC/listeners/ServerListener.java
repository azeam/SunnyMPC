package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;

import SunnyMPC.ServerScan;

public class ServerListener implements ActionListener {
    JComboBox<String> serverCombobox;
    JButton updateServers;

    public ServerListener(JComboBox<String> serverCombobox, JButton updateServers) {
        this.serverCombobox = serverCombobox;
        this.updateServers = updateServers;    
	}

	@Override
    public void actionPerformed(ActionEvent ae) {
        serverCombobox.removeAllItems();
        ServerScan.startThread(updateServers);
    }    
}