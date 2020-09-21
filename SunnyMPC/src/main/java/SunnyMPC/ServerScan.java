package SunnyMPC;

import java.io.FileWriter;
import java.io.IOException;
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

import javax.swing.SwingWorker;

public class ServerScan {
    public static void startThread() { 
        SwingWorker<String[], String[]> sw = new SwingWorker<String[], String[]>() { 
            @Override
            protected String[] doInBackground() throws Exception { 
                List<String> serversList = new ArrayList<String>();
                Enumeration<NetworkInterface> nets = null;
                try {
                    nets = NetworkInterface.getNetworkInterfaces();
                } catch (SocketException e1) {
                    e1.printStackTrace();
                }
                for (NetworkInterface netint : Collections.list(nets)) {
                    try {
                        String ip = displayInterfaceInformation(netint);
                        if (ip.length() > 1) {
                            for (int ipEnd = 1; ipEnd <= 255; ipEnd++) {
                                try {
                                Socket socket = new Socket();
                                socket.connect(new InetSocketAddress(ip + ipEnd, 6600), 100);
                                socket.close();
                                System.out.println("IP " + ip + ipEnd + " is open");
                                serversList.add(ip + ipEnd);
                            } catch (Exception e) {}
                            }
                        }
                    } catch (SocketException e1) {
                        e1.printStackTrace();
                    }
                }
                String[] servers = serversList.toArray(new String[0]);
                return servers;
            }

            @Override
            protected void done() { 
                try { 
                    String[] servers = get();
                    if (servers.length > 0) {
                        try {
                            FileWriter saveServers = new FileWriter("servers.txt");
                            for (String server : servers) {
                                saveServers.write(server + "\n");
                            }
                            saveServers.close();
                            System.out.println("Successfully saved server list.");
                          } catch (IOException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                          }
                        GUIBuilder guiBuilder = new GUIBuilder();
                        guiBuilder.fillServers(servers);
                    }
                }  
                catch (InterruptedException e) { 
                    e.printStackTrace(); 
                }  
                catch (ExecutionException e) { 
                    e.printStackTrace(); 
                } 
            } 
        };
         
    sw.execute();  
    } 

    private static String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        String ip = "";
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            // only add ipv4 and skip localhost ip range
            if (!inetAddress.toString().contains(":") && !inetAddress.toString().startsWith("/127")) {
                ip = inetAddress.toString().split("/")[1];
                String[] parts = ip.split("\\."); // escape dot or java will treat it as regex
                ip = parts[0] + "." + parts[1] + "." + parts[2] + ".";
            }
        }
        return ip;
    }
}