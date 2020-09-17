package SunnyMPC;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventListModel;

public class GUIBuilder {
    static PrintWriter out;

    public void build() {
        // main layout
        JFrame window = new JFrame("Sunny MPC");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
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
        String[] columnNames = {"Track #",
                        "Artist",
                        "Title",
                        "Album",
                        "Year", 
                        "Format",
                        "Bitrate",
                        "Length"
                    };
        Object[][] data = {
            {"Kathy", "Smith",
                "Snowboarding", 5, false},
            {"John", "Doe",
                "Rowing", 3, true},
            {"Sue", "Black",
                "Knitting", 2, false},
            {"Jane", "White",
                "Speed reading", 20, true},
            {"Joe", "Brown",
                "Pool", 10, false}
        };
        JTable table = new JTable(data, columnNames);
        // disable editing
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            private static final long serialVersionUID = 4576897713877240253L;
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        JScrollPane tableContainer = new JScrollPane(table);        
        table.setFillsViewportHeight(true);
        table.setModel(tableModel);


        // browseList
        String[] artistBrowseList = {"apple", "appricot", "acorn", "blueberry", "coconut", "chesnut", "grape"};
        EventList<String> rawList = GlazedLists.eventListOf(artistBrowseList);
        SeparatorList<String> separatorList =
                new SeparatorList(rawList, createComparator(), 0, 1000);

        JList<String> browseList = new JList(new DefaultEventListModel(separatorList));
        browseList.setCellRenderer(createListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(browseList);
        scrollPane.setBorder(null);


        // set listeners
        UpdateListener updateListener = new UpdateListener(browseList);
		updateMPDBtn.addActionListener(updateListener);
        
        // wrap up
        window.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        window.add(topPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        window.add(tableContainer, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        window.add(scrollPane, gbc);
         
        window.setPreferredSize(new Dimension(600, 600));
        window.pack();
        window.setVisible(true); 
        connect("192.168.1.87", 6600); 


        


    }

     /**
     * Creates a {@link Comparator} that compares the first letter of two given strings.
     */
    private static Comparator createComparator() {
        return new Comparator() {
            @Override
            public int compare(Object arg0, Object arg1) {
                return arg0.toString().substring(0,1).compareTo(arg1.toString().substring(0,1));
            }
        };
    }

    /**
     * Creates a renderer that can render both separators and regular items.
     */
    private static ListCellRenderer createListCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList list, Object value, int index, boolean isSelected, 
                    boolean cellHasFocus) {
                
                // call the super renderer to take care of setting the foreground and
                // background colors.
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                // if the item being renderered is a separator, then bold it, and shift
                //    in slightly.
                // else if the item being rendered is an actual list item, make it plain
                //    and shift it in more.
                if (value instanceof SeparatorList.Separator) {
                    SeparatorList.Separator separator = (SeparatorList.Separator) value;
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

    public void connect(String ip, int port) {
        // connect to socket
        try (
            Socket socket = new Socket(ip, port);
            BufferedReader serverResponse = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);
            String fromServer;
            // communicate with server
            while ((fromServer = serverResponse.readLine()) != null) {
                System.out.println(fromServer);
            }
            socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Server at " + ip + " not found.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip);
            System.exit(1);
        }
    }
}
