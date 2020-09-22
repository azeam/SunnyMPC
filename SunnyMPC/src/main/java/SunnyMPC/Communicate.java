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
    private static PrintWriter out;
    private static BufferedReader serverResponse;
    private static Socket socket;
    public static final int port = 6600;
    public static String ip;

    public static List<String> getStatus(String cmd) {
        List<String> response = new ArrayList<String>();
        String fromServer;
        if (out == null) {return response;}
        out.println(cmd);
        try {
            while ((fromServer = serverResponse.readLine()) != null && !fromServer.equals("OK")) {
            //    System.out.println(fromServer);
                if (fromServer.startsWith("ACK")) {
                    break;
                }
                if (!fromServer.startsWith("OK MPD")) {
                    response.add(fromServer);
                }
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }
        return response;
    }

    public static List<String> sendCmd(String cmd) {
        connect();
        List<String> response = getStatus(cmd);
        disconnect();
        return response;
    }

    public static void connect() {
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
    }

	public static void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
