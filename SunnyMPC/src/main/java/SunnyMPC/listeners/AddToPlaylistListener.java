package SunnyMPC.listeners;

import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.DisplayTable;
import SunnyMPC.Helper;

public class AddToPlaylistListener implements TreeSelectionListener {
    JTable tree;

    public AddToPlaylistListener(JTable tree) {
        this.tree = tree;
    }
    

	@Override
    public void valueChanged(TreeSelectionEvent arg0) {
        TreePath selectedPath = arg0.getPath();
        DefaultMutableTreeNode albumNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        DefaultMutableTreeNode artistNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(1);

        // when clicking an album, get the artist/album name and add to playlist
        if (albumNode!= null && albumNode.isLeaf()) {
            Helper helper = new Helper();
            String selectedAlbum = albumNode.toString();
            String selectedArtist = artistNode.toString();
            Communicate.sendCmd(Constants.findAddArtist + helper.escapeString(selectedArtist) + Constants.albumSpace + helper.escapeString(selectedAlbum));
            DisplayTable.displayTable();
        }
    }

   
}