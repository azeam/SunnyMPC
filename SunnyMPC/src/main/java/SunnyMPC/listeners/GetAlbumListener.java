
package SunnyMPC.listeners;

import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import SunnyMPC.Helper;

public class GetAlbumListener implements TreeExpansionListener {
    JTree tree;

    public GetAlbumListener(JTree tree) {
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
            List<String> albumStringList = helper.cleanupList("list album " + helper.escapeString(artist));
            for (String album : albumStringList) {
                DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(album);
                model.insertNodeInto(leaf, selectedNode, 0);
            }
                return null;
           }
        };
        sw.execute();
    }

    
}