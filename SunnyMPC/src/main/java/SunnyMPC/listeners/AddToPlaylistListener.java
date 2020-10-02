package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.DisplayTable;
import SunnyMPC.Helper;

public class AddToPlaylistListener implements MouseInputListener {
    JTree tree;
    JPopupMenu popup;
    JMenuItem addLabelItem;
    Helper helper = new Helper();
    Communicate com = new Communicate();
    DefaultMutableTreeNode artistNode;
    DefaultMutableTreeNode albumNode;
    DefaultMutableTreeNode trackNode;
    String selectedAlbum;
    String selectedArtist;
    String selectedTrack;
    TreePath selectedPath = null;

    public AddToPlaylistListener(JTree tree) {
        this.tree = tree;
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        // show add menu on right click
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = tree.getClosestRowForLocation(e.getX(), e.getY());
            tree.setSelectionRow(row);
            checkTreeDepth(e.getX(), e.getY());
        }
        // add track on double click
        else if (e.getClickCount() == 2) {
            selectedPath = tree.getPathForLocation(e.getX(), e.getY());
            trackNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedPath != null && trackNode.isLeaf()) {
                addTrack(selectedPath);
                DisplayTable.displayTable();
            }
        }
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent arg0) {}
    @Override
    public void mouseExited(java.awt.event.MouseEvent arg0) {}
    @Override
    public void mousePressed(java.awt.event.MouseEvent arg0) {}
    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {}
    @Override
    public void mouseDragged(java.awt.event.MouseEvent arg0) {}
    @Override
    public void mouseMoved(java.awt.event.MouseEvent arg0) {}

    private void checkTreeDepth(int x, int y) {
        selectedPath = tree.getPathForLocation(x, y);
        if (selectedPath == null) {
            return;
        }

        String addLabel = "Add " + selectedPath.getLastPathComponent().toString() + " to playlist";
        popup = new JPopupMenu();
        addLabelItem = new JMenuItem(addLabel);

        addLabelItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String[] checkBranch = selectedPath.toString().split(",");
                int pathPosition = checkBranch.length;
                switch (pathPosition) {
                    case 2:
                        addArtist(selectedPath);
                        break;
                    case 3:
                        addAlbum(selectedPath);
                        break;
                    case 4:
                        addTrack(selectedPath);    
                        break;
                }
                DisplayTable.displayTable();
            }
           });  
        popup.add(addLabelItem);
        popup.show(tree, x, y);
    }

    private void addArtist(TreePath selectedPath) {
        artistNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();       
        selectedArtist = artistNode.toString();
        com.sendCmd(Constants.findAddArtist + helper.escapeString(selectedArtist));  
    }

    private void addAlbum(TreePath selectedPath) {
        albumNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();       
        artistNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(1);
        selectedAlbum = albumNode.toString();
        selectedArtist = artistNode.toString();
        com.sendCmd(Constants.findAddArtist + helper.escapeString(selectedArtist) + Constants.albumSpace + helper.escapeString(selectedAlbum));
    }

    private void addTrack(TreePath selectedPath) {
        trackNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        albumNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(2);        
        artistNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(1);
        selectedAlbum = albumNode.toString();
        selectedArtist = artistNode.toString();
        selectedTrack = trackNode.toString();
        com.sendCmd(Constants.findAddArtist + helper.escapeString(selectedArtist) + Constants.albumSpace + helper.escapeString(selectedAlbum) + Constants.titleSpace + helper.escapeString(selectedTrack));        
    }
}