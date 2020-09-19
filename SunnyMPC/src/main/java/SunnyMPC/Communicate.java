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

    public static List<String> getList(String cmd) {
        List<String> response = new ArrayList<String>();
        String fromServer;
        out.println(cmd);
        try {
             // TODO: fix better
            serverResponse.readLine(); // skip first MPD OK line
            serverResponse.readLine(); // skip second MPD OK line
            while ((fromServer = serverResponse.readLine()) != null && !fromServer.equals("OK")) {
                if (fromServer.startsWith("ACK")) {
                    break;
                }
                response.add(fromServer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getPlaylist(String cmd) {
        String fromServer;
        String response = "";
        out.println(cmd);
        try {
            while ((fromServer = serverResponse.readLine()) != null && !fromServer.equals("OK")) {
                if (fromServer.startsWith("ACK")) {
                    break;
                }
                response += fromServer;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static boolean sendCmd(String cmd) {
        try {
            out.println(cmd);
            String fromServer;
            while ((fromServer = serverResponse.readLine()) != null && !fromServer.equals("OK")) {
                System.out.println(fromServer);
                if (fromServer.startsWith("ACK")) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void connect(String ip, int port) {
        // connect to socket
        try {    
            Socket socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            serverResponse = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Server at " + ip + " not found.");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip);
        }
    }
}
