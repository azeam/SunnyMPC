package SunnyMPC;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

public class ServerScan {
    public static void startThread(JButton updateServers) { 
        GUIBuilder guiBuilder = new GUIBuilder();
        updateServers.setText("Searching...");
        updateServers.setEnabled(false);
        SwingWorker<String[], String> sw = new SwingWorker<String[], String>() { 
            @Override
            protected String[] doInBackground() throws Exception { 
                List<String> serversList = new ArrayList<String>();
                Enumeration<NetworkInterface> nets = null;
                // get network interfaces, can be connected using wifi/ethernet
                try {
                    nets = NetworkInterface.getNetworkInterfaces();
                } catch (SocketException e1) {
                    e1.printStackTrace();
                }
                for (NetworkInterface netint : Collections.list(nets)) {
                    try {
                        // get ip base for each connected interface
                        String ip = getLANRange(netint);
                        if (ip.length() > 1) {
                            // checking 1-255 range
                            for (int ipEnd = 1; ipEnd <= 255; ipEnd++) {
                                try {
                                Socket socket = new Socket();
                                // connect to each IP address on port 6600 and confirm the "MPD OK" response, if so add to list
                                socket.connect(new InetSocketAddress(ip + ipEnd, Constants.port), 100); // 100 ms seems enough on my network
                                String fromServer;
                                BufferedReader serverResponse = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                try {
                                    if ((fromServer = serverResponse.readLine()) != null && fromServer.startsWith(Constants.okMpd)) {
                                        System.out.println("MPD server at " + ip + ipEnd + " found");
                                        serversList.add(ip + ipEnd);
                                        publish(ip + ipEnd);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                socket.close();
                            } catch (Exception e) {}
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
                String[] servers = serversList.toArray(new String[0]);
                return servers;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String server : chunks) {
                    guiBuilder.fillServers(server);
                }
            }

            @Override
            protected void done() { 
                try { 
                    String[] servers = get();
                    if (servers.length > 0) {
                        try {
                            FileWriter saveServers = new FileWriter(Constants.servers);
                            for (String server : servers) {
                                saveServers.write(server + "\n");
                            }
                            saveServers.close();
                            System.out.println("Successfully saved server list.");
                          } catch (IOException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                          }
                       
                    }
                    else {
                        // TODO: show no servers found
                    }
                }  
                catch (InterruptedException e) { 
                    e.printStackTrace(); 
                }  
                catch (ExecutionException e) { 
                    e.printStackTrace(); 
                } 
                updateServers.setText("Find servers");
                updateServers.setEnabled(true);
            } 
        };
    sw.execute();  
    } 

    private static String getLANRange(NetworkInterface netint) throws SocketException {
        String ip = "";
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            // only check ipv4 and skip localhost ip range
            if (!inetAddress.toString().contains(":") && !inetAddress.toString().startsWith("/127")) {
                ip = inetAddress.toString().split("/")[1];
                String[] parts = ip.split("\\."); // escape dot or java will treat it as regex
                ip = parts[0] + "." + parts[1] + "." + parts[2] + ".";
            }
        }
        return ip;
    }
}