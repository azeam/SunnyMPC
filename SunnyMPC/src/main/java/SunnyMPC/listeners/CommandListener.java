package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import SunnyMPC.Communicate;

public class CommandListener implements ActionListener {
    private String cmd;
    
    public CommandListener(String cmd) {
        this.cmd = cmd;
	}

	@Override
    public void actionPerformed(ActionEvent ae) {
        Communicate.sendCmd(cmd);
    }
    
}