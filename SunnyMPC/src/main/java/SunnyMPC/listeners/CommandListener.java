package SunnyMPC.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import SunnyMPC.Communicate;
import SunnyMPC.Constants;
import SunnyMPC.DisplayTable;
import SunnyMPC.GUIBuilder;
import SunnyMPC.TrackInfo;

public class CommandListener implements ActionListener {
    private String cmd;
    private JTable table;
    GUIBuilder guiBuilder = new GUIBuilder();
            
    public CommandListener(String cmd, JTable table) {
        this.cmd = cmd;
        this.table = table;
	}

	@Override
    public void actionPerformed(ActionEvent ae) {
        Communicate com = new Communicate();
        com.sendCmd(cmd);     

        // update track data when going back/forward and send it to the track info handler for display
        if (cmd.equals(Constants.next) || cmd.equals(Constants.previous)) {
            TrackInfo.run = false;
            TrackInfo.getTrackInfo(table);
        }
        else if (cmd.equals(Constants.clear)) {
            DisplayTable.displayTable();
        }
        else if (cmd.equals(Constants.stop)) {
            TrackInfo.run = false;
            guiBuilder.showAlbumImage(Constants.noImage);
            guiBuilder.setTrackText("");
        }
        else if (cmd.equals(Constants.reset)) {
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
            table.setRowSorter(sorter);
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            
            int columnIndexToSort = 0;
            sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));
            
            sorter.setSortKeys(sortKeys);
            sorter.sort();
        }
    }
}