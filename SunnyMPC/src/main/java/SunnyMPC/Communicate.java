package SunnyMPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Communicate {
    static PrintWriter out;
    static BufferedReader serverResponse;
    static Socket socket;
    static int sPort;
    static String sIp;

    public static List<String> sendCmd(String cmd) {
        Communicate.connect(sIp, sPort);
        List<String> response = new ArrayList<String>();
        String fromServer;
        out.println(cmd);
        try {
            while ((fromServer = serverResponse.readLine()) != null && !fromServer.equals("OK")) {
                System.out.println(fromServer);
                if (fromServer.startsWith("ACK")) {
                    break;
                }
                if (!fromServer.startsWith("OK MPD")) {
                    response.add(fromServer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void connect(String ip, int port) {
        sIp = ip;
        sPort = port;
        Runnable runnable =
        () -> { System.out.println("Lambda Runnable running"); };
        // connect to socket
        try {    
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            serverResponse = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Server at " + ip + " not found.");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip);
        }
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
