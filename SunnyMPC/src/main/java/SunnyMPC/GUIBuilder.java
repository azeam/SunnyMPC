package SunnyMPC;

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
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
    private GridBagConstraints gbc;
    private String[] headers = { "Title", "Album", "Artist", "Duration" };
    static DefaultMutableTreeNode root = new DefaultMutableTreeNode("artists");

    private DefaultTableModel tableModel = new DefaultTableModel() {
        private static final long serialVersionUID = 1L;

        // disable table editing
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public void fillServers(String[] servers) {
        serverCombobox.removeAllItems();
        serverCombobox.addItem("");
        for (String server : servers) {
            serverCombobox.addItem(server);
        }
        serverCombobox.addItemListener(new ServerChangeListener());
    }

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

    public void setTableData(List<Object[]> rowData) {
        tableModel.addColumn("id", rowData.get(0));
        tableModel.addColumn("mbAlbum", rowData.get(1));
        tableModel.addColumn(headers[0], rowData.get(2));
        tableModel.addColumn(headers[1], rowData.get(3));
        tableModel.addColumn(headers[2], rowData.get(4));
        tableModel.addColumn(headers[3], rowData.get(5));
        table.setModel(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide id column
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

        // artist list
        tree = new JTree(root);
        tree.expandRow(0);
        tree.setRootVisible(false); // hide root node
        artistContainer = new JScrollPane(tree);

        // bottom row
        JPanel controlPanel = new JPanel();
        JButton stopBtn = new JButton("Stop");
        JButton nextBtn = new JButton("Next");
        JButton previousBtn = new JButton("Previous");
        Box controlBox = Box.createHorizontalBox();
        controlBox.add(stopBtn);
        controlBox.add(nextBtn);
        controlBox.add(previousBtn);
        controlPanel.add(controlBox);

        // set listeners
        updateMPDBtn.addActionListener(new CommandListener("update"));
        updateServers.addActionListener(new ServerListener()); // TODO: spinner while updating
        stopBtn.addActionListener(new CommandListener("stop"));
        nextBtn.addActionListener(new CommandListener("next"));
        previousBtn.addActionListener(new CommandListener("previous"));
        tree.addTreeSelectionListener(new AddToPlaylistListener(table));
        tree.addTreeExpansionListener(new GetAlbumListener(tree));
        table.getSelectionModel().addListSelectionListener(new PlayTrackListener(table));

        // default album cover
        albumPic = new JLabel(new ImageIcon("noimage.jpg"));

        // wrap up
        window.setLayout(new GridBagLayout());
        addPart(window, topPanel, 1, 0, 1, 1, 0.7, 0.7);
        addPart(window, tableContainer, 1, 1, 1, 1, 0.7, 0.3);
        addPart(window, artistContainer, 0, 1, 1, 2, 0.3, 1.0);
        addPart(window, controlPanel, 1, 2, 1, 2, 0.3, 1.0);
        addPart(window, albumPic, 0, 2, 1, 2, 0.3, 1.0 );    
        window.pack();
        window.setVisible(true);
        findServers();
    }

    public void showAlbumImage(String path) {
        System.out.println(path);
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

    private void findServers() {
        List<String> serverList = new ArrayList<String>();
        try {
            File savedServers = new File("servers.txt");
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
