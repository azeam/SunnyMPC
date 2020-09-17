package SunnyMPC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;

public class UpdateListener implements ActionListener {

    private JList<String> browseList;

    public UpdateListener(JList<String> browseList) {
        this.browseList = browseList;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("test");
        GUIBuilder.out.println("list artist");
    }
}