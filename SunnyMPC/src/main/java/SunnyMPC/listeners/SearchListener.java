
package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jdesktop.swingx.JXTree;

public class SearchListener implements ActionListener {
    JXTree tree;

    public SearchListener(JXTree tree) {
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        System.out.println(arg0.toString());
    }

    
}