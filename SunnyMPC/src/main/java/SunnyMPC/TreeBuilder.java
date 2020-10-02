package SunnyMPC;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JTable;
import javax.swing.SwingWorker;

public class TreeBuilder {
    GUIBuilder gui = new GUIBuilder();

    JTable table;

    public TreeBuilder(JTable table) {
        this.table = table;
    }

    public void getServerData() {
        SwingWorker<List<String>, Boolean> sw = new SwingWorker<List<String>, Boolean>() {
        @Override
        protected List<String> doInBackground() throws Exception {
            Helper helper = new Helper();
            List<String> artistStringList = helper.cleanupList(Constants.listartists);
            return artistStringList;
        }

        @Override
        protected void done() {
            try {
                gui.fillAlbumList(get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
                TrackInfo.getTrackInfo(table);
                DisplayTable.displayTable();
            }
        };
        sw.execute();
    }
}