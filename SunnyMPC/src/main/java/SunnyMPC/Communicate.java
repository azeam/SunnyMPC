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
    private PrintWriter out;
    private BufferedReader serverResponse;
    private Socket socket;
    public static String ip;

    // get status, will keep the connection alive, used for long polling the current track information
    public List<String> getStatus(String cmd) {
        List<String> response = new ArrayList<String>();
        String fromServer;
        if (out == null) {return response;}
        out.println(cmd);
        try {
            while ((fromServer = serverResponse.readLine()) != null && !fromServer.equals(Constants.ok)) {
//          System.out.println(fromServer);
                if (fromServer.startsWith(Constants.ack)) {
                    break;
                }
                if (!fromServer.startsWith(Constants.okMpd)) {
                    response.add(fromServer);
                }
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }
        return response;
    }

    // send simple command, this will connect and disconnect from the socket
    public List<String> sendCmd(String cmd) {
        connect();
        List<String> response = getStatus(cmd);
        disconnect();
        return response;
    }

    // connect to socket
    public void connect() {
        try {    
            socket = new Socket(ip, Constants.port);
            out = new PrintWriter(socket.getOutputStream(), true);
            serverResponse = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Server at " + ip + " not found.");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip);
        }
    }

    // disconnect from socket
	public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
