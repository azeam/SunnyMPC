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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import ca.odell.glazedlists.EventList;

public class GUIBuilder {
    private final int hGap = 5;
    private final int vGap = 5;
    static JTable table;
    static EventList<String> artistList;
    private GridBagConstraints gbc;

    private ListSelectionListener trackListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent event) {
            // get value from hidden id column
            // check if row exists because removing column when updating data will trigger
            // valuechanged, causing IOB
            int i = table.getSelectedRow();
            if (i >= 0) {
                Communicate.sendCmd("playid " + table.getModel().getValueAt(table.getSelectedRow(), 0).toString());
            }
        }
    };

    private DefaultTableModel tableModel = new DefaultTableModel() {
        private static final long serialVersionUID = 1L;
        @Override
        public boolean isCellEditable(int row, int column) {
           return false;
        }
    };

    public void setTableData(String idCol, Object[] idData, String titleCol, Object[] titleData) {
        // disable editing
        tableModel.addColumn(idCol, idData);
        tableModel.addColumn(titleCol, titleData);
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
        table = new JTable();  
        JScrollPane tableContainer = new JScrollPane(table);        
        table.setFillsViewportHeight(true);
        table.getSelectionModel().addListSelectionListener(trackListener); 

        // browseList
        Commands cmds = new Commands();
        List<String> artistStringList = cmds.getList("list albumartist");
       
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("artists");
        //create the child nodes
        for (String artist : artistStringList) {
            DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
            root.add(artistNode);
    
            DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode("album");
            artistNode.add(albumNode);
    
            // working search    List<String> albumStringList = cmds.getList("find \"(artist == \'" + artist + "\\\')\"");
    
            // super slow to add all albums at start, but works, only add if clicking
            /*
                List<String> albumStringList = cmds.getList("list album \"" + artist + "\"");
                for (String album : albumStringList) {

                    DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                    artistNode.add(albumNode);
                }
            */
        }
         
        JTree tree = new JTree(root);
        tree.expandRow(0);
        tree.setRootVisible(false);
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
