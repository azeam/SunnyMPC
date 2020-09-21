package SunnyMPC;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class ServerScan {
    public void scan() {
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
                    } catch (Exception e) {
                    }
                    }
                }
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        }    
    }
       
    private String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        String ip = "";
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            // only add ipv4 and skip localhost ip range
            if (!inetAddress.toString().contains(":") && !inetAddress.toString().startsWith("/127")) {
                ip = inetAddress.toString().split("/")[1];
                System.out.println(ip);
                String[] parts = ip.split("\\.");
                ip = parts[0] + "." + parts[1] + "." + parts[2] + ".";
                System.out.println(ip);
            }
        }
        return ip;
    }
}