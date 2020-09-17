package SunnyMPC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UpdateListener implements ActionListener {

	@Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println(getArtistList().toString());
    }

	public List<String> getArtistList() {
        return sendCmd("192.168.1.87", 6600, "list artist");
    }
    
    public List<String> sendCmd(String ip, int port, String cmd) {
        List<String> response = new ArrayList<String>();
        // connect to socket
        try (    
            Socket socket = new Socket(ip, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader serverResponse = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String fromServer;
            out.println(cmd);
            // communicate with server
            serverResponse.readLine(); // skip first MPD OK line
            serverResponse.readLine(); // skip second MPD OK line
            while ((fromServer = serverResponse.readLine()) != null) {
                if (fromServer.equals("OK")) {
                    return response;
                }
                response.add(fromServer.substring(fromServer.indexOf(" ") + 1));
            }
            socket.close();

        } catch (UnknownHostException e) {
            System.err.println("Server at " + ip + " not found.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip);
            System.exit(1);
        }
        return response;
    }
}