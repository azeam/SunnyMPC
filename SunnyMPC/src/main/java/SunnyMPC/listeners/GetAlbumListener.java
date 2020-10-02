
package SunnyMPC.listeners;

import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;

import SunnyMPC.Helper;

public class GetAlbumListener implements TreeExpansionListener {
    JXTree tree;

    public GetAlbumListener(JXTree tree) {
        this.tree = tree;
    }
    
    @Override
    public void treeCollapsed(TreeExpansionEvent arg0) {}

    @Override
    public void treeExpanded(TreeExpansionEvent arg0) {       
        // add albums on expand
        TreePath selectedPath = arg0.getPath();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        if (selectedNode.getChildCount() == 1) {
            String artist = selectedNode.toString();
            getServerData(model, selectedNode, artist);
        }
    }

    private void getServerData(DefaultTreeModel model, DefaultMutableTreeNode selectedNode, String artist) {
        SwingWorker<Boolean, Boolean> sw = new SwingWorker<Boolean, Boolean>() { 
            @Override
            protected Boolean doInBackground() throws Exception { 
                Helper helper = new Helper();
                
                // get albums by artist
                List<String> albumStringList = helper.cleanupList("list album " + helper.escapeString(artist));
                for (String album : albumStringList) {
                    DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                    model.insertNodeInto(albumNode, selectedNode, 0);

                    // get titles for album
                    List<String> trackStringList = helper.cleanupList("list title album " + helper.escapeString(album));
                    for (String trackString : trackStringList) {
                        DefaultMutableTreeNode trackNode = new DefaultMutableTreeNode(trackString);
                        model.insertNodeInto(trackNode, albumNode, 0);
                    }
                }
                return null;
            }
        };
        sw.execute();
    }

    
}