package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import SunnyMPC.ServerScan;

public class ServerListener implements ActionListener {
	@Override
    public void actionPerformed(ActionEvent ae) {
        ServerScan.startThread();
    }    
}