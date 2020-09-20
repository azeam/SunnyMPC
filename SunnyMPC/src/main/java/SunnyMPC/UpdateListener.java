package SunnyMPC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ae) {
        Communicate.sendCmd("update");
    }
    
}