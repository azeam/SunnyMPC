package SunnyMPC;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.swing.DefaultEventListModel;

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

    private ListEventListener artistListener = new ListEventListener() {
        @Override
        public void listChanged(ListEvent listChanges) {
            System.out.println("playid ");
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
        List<String> artistStringList = cmds.getArtistList();
        String[] artistBrowseList = artistStringList.toArray(new String[0]);
        artistList = GlazedLists.eventListOf(artistBrowseList);
        SeparatorList<String> separatorList =
                new SeparatorList<String>(artistList, createComparator(), 0, 100000);
        JList<String> browseList = new JList<String>(new DefaultEventListModel<String>(separatorList));
        browseList.setCellRenderer(createListCellRenderer());
        JScrollPane browsePane = new JScrollPane(browseList);
        browsePane.setBorder(null);
        artistList.addListEventListener(artistListener);
        
        
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
        addPart(window, browsePane, 0, 1, 1, 2, 0.3, 1.0 );
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

    /**
     * Creates a {@link Comparator} that compares the first letter of two given
     * strings.
     */
    private static Comparator<String> createComparator() {
        return new Comparator<String>() {
            @Override
            public int compare(String arg0, String arg1) {
                return arg0.substring(0,1).compareTo(arg1.substring(0,1));
            }
        };
    }

    private static DefaultListCellRenderer createListCellRenderer() {
        return new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, 
                    boolean cellHasFocus) {
                

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                // if the item being renderered is a separator, then bold it, and shift
                //    in slightly.
                // else if the item being rendered is an actual list item, make it plain
                //    and shift it in more.
                if (value instanceof SeparatorList.Separator) {
                    SeparatorList.Separator<?> separator = (SeparatorList.Separator<?>) value;
                    label.setText(separator.getGroup().get(0).toString().substring(0,1));
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                    label.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
                } else {
                    label.setFont(label.getFont().deriveFont(Font.PLAIN));
                    label.setBorder(BorderFactory.createEmptyBorder(0,15,0,0));
                }

                return label;
            }
        };
    }

  
}
