package SunnyMPC;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.border.DropShadowBorder;

import SunnyMPC.listeners.AddToPlaylistListener;
import SunnyMPC.listeners.CommandListener;
import SunnyMPC.listeners.GetAlbumListener;
import SunnyMPC.listeners.PlayTrackListener;
import SunnyMPC.listeners.ServerChangeListener;
import SunnyMPC.listeners.ServerListener;

public class GUIBuilder {
    private final int hGap = 5;
    private final int vGap = 5;
    static JTable table;
    static JTree tree;
    static JComboBox<String> serverCombobox;
    static List<String> artistList;
    static JScrollPane artistContainer;
    static JFrame window;
    static JLabel albumPic;
    static JTextPane trackInfoText;
    private GridBagConstraints gbc;
    private String[] headers = { "Title", "Album", "Artist", "Duration" };
    static DefaultMutableTreeNode root = new DefaultMutableTreeNode("artists");

    // (re-)populate server list
    public void fillServers(String[] servers) {
        serverCombobox.removeAllItems();
        serverCombobox.addItem("");
        for (String server : servers) {
            serverCombobox.addItem(server);
        }
        serverCombobox.addItemListener(new ServerChangeListener(table));
    }

    // (re-)populate left pane with artist
    public void fillAlbumList(List<String> artistStringList) {
        root.removeAllChildren();
        for (String artist : artistStringList) {
            DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
            root.add(artistNode);
            // add empty line to show chevron, hacky but works
            DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode("");
            artistNode.add(albumNode);
        }
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.reload();
    }

    // populate table with data
    public void setTableData(List<Object[]> rowData) {
        tableModel.addColumn("id", rowData.get(0));
        tableModel.addColumn("mbAlbum", rowData.get(1)); // TODO: I think this can be removed now
        tableModel.addColumn(headers[0], rowData.get(2));
        tableModel.addColumn(headers[1], rowData.get(3));
        tableModel.addColumn(headers[2], rowData.get(4));
        tableModel.addColumn(headers[3], rowData.get(5));
        table.setModel(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide id column, no need to display, used for sending playid cmd
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide musicbrainz album id column
    }

    public void build() {
        // main layout
        window = new JFrame("Sunny MPC");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(hGap, vGap, hGap, vGap);

        // top row
        JPanel topPanel = new JPanel();
        serverCombobox = new JComboBox<String>();
        JButton updateMPDBtn = new JButton("Update MPD");
        JButton updateServers = new JButton("Update servers");
        Box topBox = Box.createHorizontalBox();
        topBox.add(updateMPDBtn);
        topBox.add(serverCombobox);
        topBox.add(updateServers);
        topPanel.add(topBox);

        // table
        Object[][] rows = new Object[][] {};
        table = new JTable(rows, headers);
        JScrollPane tableContainer = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true); 
        // TODO: enable re-ordering the playlist, probably by setting mpd "pos" from row count

        // artist list
        tree = new JTree(root);
        tree.expandRow(0);
        tree.setRootVisible(false); // hide root node
        artistContainer = new JScrollPane(tree);

        // bottom row
        JPanel controlPanel = new JPanel();
        JButton clearBtn = new JButton("Clear playlist");
        JButton stopBtn = new JButton("Stop");
        JButton nextBtn = new JButton("Next");
        JButton previousBtn = new JButton("Previous");
        JButton resetBtn = new JButton("Reset sort");

        // button padding
        clearBtn.setMargin(new Insets(5, 10, 5, 10));
        stopBtn.setMargin(new Insets(5, 10, 5, 10));
        nextBtn.setMargin(new Insets(5, 10, 5, 10));
        previousBtn.setMargin(new Insets(5, 10, 5, 10));
        updateMPDBtn.setMargin(new Insets(5, 10, 5, 10));
        updateServers.setMargin(new Insets(5, 10, 5, 10));
        resetBtn.setMargin(new Insets(5, 10, 5, 10));
        
        // button container
        Box controlBox = Box.createHorizontalBox();
        controlBox.add(clearBtn);
        controlBox.add(previousBtn);
        controlBox.add(stopBtn);
        controlBox.add(nextBtn);
        controlBox.add(resetBtn);
        controlPanel.add(controlBox);

        // track info
        trackInfoText = new JTextPane();
        trackInfoText.setContentType("text/html");
        trackInfoText.setOpaque(false);
        trackInfoText.setEditable(false);
        // set fixed dimension to prevent resizing when text width changes (every bitrate/second update)
        Dimension textDimension = new Dimension(300, 50);
        trackInfoText.setMinimumSize(textDimension);
        trackInfoText.setPreferredSize(textDimension);
        trackInfoText.setMaximumSize(textDimension);

        // set listeners
        clearBtn.addActionListener(new CommandListener(Constants.clear, table));
        updateMPDBtn.addActionListener(new CommandListener(Constants.update, table));
        updateServers.addActionListener(new ServerListener()); // TODO: spinner while updating
        stopBtn.addActionListener(new CommandListener(Constants.stop, table));
        nextBtn.addActionListener(new CommandListener(Constants.next, table));
        resetBtn.addActionListener(new CommandListener(Constants.reset, table));
        previousBtn.addActionListener(new CommandListener(Constants.previous, table));
        tree.addTreeSelectionListener(new AddToPlaylistListener(table));
        tree.addTreeExpansionListener(new GetAlbumListener(tree));
        table.addMouseListener(new PlayTrackListener(table));
            
      
        // default album cover
        JPanel imageContainer = new JPanel();
        albumPic = new Shadow(new ImageIcon());
        showAlbumImage(Constants.noImage);
        albumPic.setAlignmentX(Shadow.CENTER_ALIGNMENT);
        imageContainer.add(albumPic);

        // wrap up
        window.setLayout(new GridBagLayout());
        addPart(window, topPanel, 0, 0, 1, 1, 0, 0);
        addPart(window, tableContainer, 1, 1, 3, 1, 1, 0.3);
        addPart(window, artistContainer, 0, 1, 1, 1, 0, 0);
        addPart(window, controlPanel, 1, 0, 1, 2, 0.3, 1.0);
        addPart(window, imageContainer, 0, 2, 1, 0, 0, 0);
        addPart(window, trackInfoText, 1, 2, 1, 0, 0, 0);
        window.pack();
        window.setSize(new Dimension(1000, 900));
        window.setVisible(true);
        findServers();
    }

    private class Shadow extends JXLabel{
        private static final long serialVersionUID = -3042310413725411334L;

        public Shadow(ImageIcon icon) {
            DropShadowBorder shadow = new DropShadowBorder();
            shadow.setShowLeftShadow(false);
            shadow.setShowRightShadow(true);
            shadow.setShowBottomShadow(true);
            shadow.setShowTopShadow(false);
            this.setBorder(shadow);
        }
    }

    // update cover image
    public void showAlbumImage(String path) {
        BufferedImage cover = null;
        try {
            cover = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cover != null) {
            ImageIcon coverIcon = new ImageIcon(cover);
            albumPic.setIcon(coverIcon);
        }
    }

    // update text with current track info
    public void setTrackText(String info) {
        trackInfoText.setText(info);
    }

    // check if servers are saved, otherwise search for them
    private void findServers() {
        List<String> serverList = new ArrayList<String>();
        try {
            File savedServers = new File(Constants.servers);
            Scanner sc = new Scanner(savedServers);
            while (sc.hasNextLine()) {
              String data = sc.nextLine();
              if (data.length() > 0) {
                serverList.add(data);
              }
            }
            sc.close();
        } catch (FileNotFoundException e) {}

        String[] servers = serverList.toArray(new String[0]);
        if (servers.length > 0) {
            fillServers(servers);    
        } else {
            System.out.println("No servers saved, auto-starting search");
            ServerScan.startThread();
        }     
    }

    private DefaultTableModel tableModel = new DefaultTableModel() {
        private static final long serialVersionUID = 1L;

        // disable table editing
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    // set size etc. of each gui element
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
