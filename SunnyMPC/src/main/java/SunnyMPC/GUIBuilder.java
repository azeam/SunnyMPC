package SunnyMPC;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.google.gson.Gson;

import SunnyMPC.listeners.*;
import ca.odell.glazedlists.EventList;

public class GUIBuilder {
    private final int hGap = 5;
    private final int vGap = 5;
    static JTable table;
    static JTree tree;
    static EventList<String> artistList;
    private GridBagConstraints gbc;
    private String[] headers = {"Title", "Album", "Artist", "Duration"};

    private DefaultTableModel tableModel = new DefaultTableModel() {
        private static final long serialVersionUID = 1L;
        // disable table editing
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public void setTableData(List<String> rowData) {
        Integer[] ids = new Integer[rowData.size()];
        String[] titles = new String[rowData.size()];
        String[] artists = new String[rowData.size()];
        String[] albums = new String[rowData.size()];
        String[] time = new String[rowData.size()];

        Gson gson = new Gson();
        Helper helper = new Helper();
        int i = 0;
        for (String s : rowData) {    
            Track track = gson.fromJson(s, Track.class);
            ids[i] = track.getId();
            titles[i] = track.getTitle();
            artists[i] = track.getArtist();
            albums[i] = track.getAlbum();
            time[i] = helper.getMinutes(track.getTime());
            i++;
        }
        tableModel.addColumn("id", ids);
        tableModel.addColumn(headers[0], titles);
        tableModel.addColumn(headers[1], albums);
        tableModel.addColumn(headers[2], artists);
        tableModel.addColumn(headers[3], time);
        table.setModel(tableModel); 
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide id column  
    }

    

    public void build() {
        // main layout
        JFrame window = new JFrame("Sunny MPC");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gbc = new GridBagConstraints ();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;   
        gbc.insets = new Insets( hGap, vGap, hGap, vGap ); 

        // top row
        JPanel topPanel = new JPanel(); 
        JComboBox<String> serverCombobox = new JComboBox<String>();
        JButton updateMPDBtn = new JButton("Update MPD");
        Box topBox = Box.createHorizontalBox();
        String[] serverList = { "Server 1", "Server 2", "Server 3" };
        for(String server : serverList) {
            serverCombobox.addItem(server);
        }
        topBox.add(updateMPDBtn);
        topBox.add(serverCombobox);
        topPanel.add(topBox);

        // table
        Object[][] rows = new Object[][] {};
        table = new JTable(rows, headers);
        JScrollPane tableContainer = new JScrollPane(table);        
        table.setFillsViewportHeight(true);

        // artist list
        Helper helper = new Helper();
        List<String> artistStringList = helper.cleanupList("list albumartist");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("artists");
        for (String artist : artistStringList) {
            DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
            root.add(artistNode);
            // add empty line to show chevron
            DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode("");
            artistNode.add(albumNode);
        }
        tree = new JTree(root);
        tree.expandRow(0);
        tree.setRootVisible(false); // hide root node
        
        JScrollPane artistContainer = new JScrollPane(tree); 
        
        // top row
        JPanel controlPanel = new JPanel(); 
        JButton playBtn = new JButton("Play");
        JButton nextBtn = new JButton("Next");
        JButton previousBtn = new JButton("Previous");
        Box controlBox = Box.createHorizontalBox();
        controlBox.add(playBtn);
        controlBox.add(nextBtn);
        controlBox.add(previousBtn);
        controlPanel.add(controlBox);

        // set listeners
        UpdateListener updateListener = new UpdateListener();
        updateMPDBtn.addActionListener(updateListener);
        AddToPlaylistListener addToPlaylistListener = new AddToPlaylistListener(table);
        tree.addTreeSelectionListener(addToPlaylistListener);
        GetAlbumListener getAlbumListener = new GetAlbumListener(tree);
        tree.addTreeExpansionListener(getAlbumListener);
        PlayTrackListener playTrackListener = new PlayTrackListener(table);
        table.getSelectionModel().addListSelectionListener(playTrackListener); 
        
        // wrap up
        window.setLayout(new GridBagLayout());
        addPart(window, topPanel, 1, 0, 1, 1, 0.7, 0.7 );
        addPart(window, tableContainer, 1, 1, 1, 1, 0.7, 0.3 );
        addPart(window, artistContainer, 0, 1, 1, 2, 0.3, 1.0 );
        addPart(window, controlPanel, 1, 2, 1, 2, 0.3, 1.0 );
        window.pack();
        window.setVisible(true); 
    }

     private void addPart(JFrame window, JComponent comp, int x, int y, int gWidth, int gHeight, double weightx, double weighty) {
            gbc.gridx = x;
            gbc.gridy = y;
            gbc.gridwidth = gWidth;
            gbc.gridheight = gHeight;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = weightx;
            gbc.weighty = weighty;      
            window.add(comp, gbc);
    }

   

  
}
