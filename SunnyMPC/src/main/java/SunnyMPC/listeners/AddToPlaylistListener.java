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


    // TODO: worker class
    // TODO: findadd track
    // TODO: add artist
    // TODO: only add on explicit selection with a menu, not when clicking the item
    // TODO: use worker class for search as well

	@Override
    public void valueChanged(TreeSelectionEvent arg0) {
        Helper helper = new Helper();
        Communicate com = new Communicate();    
        DefaultMutableTreeNode artistNode;
        DefaultMutableTreeNode albumNode;
        DefaultMutableTreeNode trackNode;
        String selectedAlbum;
        String selectedArtist;
        String selectedTrack;

        TreePath selectedPath = arg0.getPath();
        String[] checkBranch = selectedPath.toString().split(",");
        int pathPosition = checkBranch.length;
        switch (pathPosition) {
            case 2:
                // artist
                artistNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();       
                selectedArtist = artistNode.toString();
                com.sendCmd(Constants.findAddArtist + helper.escapeString(selectedArtist));        
                break;
            case 3:
                //album
                albumNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();       
                artistNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(1);
                selectedAlbum = albumNode.toString();
                selectedArtist = artistNode.toString();
                com.sendCmd(Constants.findAddArtist + helper.escapeString(selectedArtist) + Constants.albumSpace + helper.escapeString(selectedAlbum));        
                break;
            case 4:
                // track
                trackNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                albumNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(2);        
                artistNode = (DefaultMutableTreeNode) selectedPath.getPathComponent(1);
                selectedAlbum = albumNode.toString();
                selectedArtist = artistNode.toString();
                selectedTrack = trackNode.toString();
                // TODO: sets the previous track, probably because of the TrackBuilder first case, fix
                com.sendCmd(Constants.findAddArtist + helper.escapeString(selectedArtist) + Constants.albumSpace + helper.escapeString(selectedAlbum) + " title " + helper.escapeString(selectedTrack));        
                break;
        }
        // when clicking an album, get the artist/album name and add to playlist   
        DisplayTable.displayTable();
    }
}