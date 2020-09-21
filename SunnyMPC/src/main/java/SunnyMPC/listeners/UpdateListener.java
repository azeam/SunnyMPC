package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import SunnyMPC.Communicate;

public class UpdateListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ae) {
        Communicate.sendCmd("update");
    }
    
}