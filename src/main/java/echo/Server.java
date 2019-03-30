package echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(23333);
            System.out.println("Listening on port: "+serverSocket.getLocalPort());

            Broader broader = new Broader();

            try {
                while (true) { 
                    Socket socket = new Socket();
                    socket = serverSocket.accept();
                    
                    broader.addSocket(socket);
                    
                }
            } catch(IOException e) {
                e.printStackTrace();
                System.err.println("Error");
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error");
        }
    }
}
class MSocket {
    Socket socket;
    String username;
    BufferedReader socketIn;
    PrintWriter socketOut;
    MSocket(Socket socket) {
        try {
            this.socket = socket;
            this.socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketOut = new PrintWriter(socket.getOutputStream());
            this.username = socketIn.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class Broader {
    private List<MSocket> sockets;
    Broader() {
        sockets = new ArrayList<>();
    }
    public boolean addSocket(Socket socket) {
        // try {
            MSocket mSocket = new MSocket(socket);
            sockets.add(mSocket);
            System.out.println(
                "accept connection from: " + socket.getInetAddress().getHostAddress() + 
                ", name: " + mSocket.username);
            
            Echor echor = new Echor(mSocket, this);
            echor.start();
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     return false;
        // }
        return true;
    }
    public boolean delSocket(MSocket mSocket) {
        sockets = sockets.stream().filter(item -> item != mSocket).collect(Collectors.toList());
        try {
            for (MSocket p : sockets) {
                Socket s = p.socket;
                PrintWriter socketOut = p.socketOut;
                socketOut.println(mSocket.username + " quit the Channel.");
                socketOut.flush();
                // socketOut.close();
            }

            mSocket.socketOut.println("stop");
            mSocket.socketOut.flush();

            mSocket.socketIn.close();
            mSocket.socketOut.close();
            mSocket.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void broad(Date time, String sender, String message) {
        // try {
            for (MSocket p : sockets) {
                Socket socket = p.socket;
                PrintWriter socketOut = p.socketOut;
                socketOut.println(sender + ": " + message);
                socketOut.flush();
            }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
class Echor extends Thread {
    MSocket mSocket;
    Broader broader;
    Echor(MSocket mSocket, Broader broader) {
        this.mSocket = mSocket;
        this.broader = broader;
    }
    @Override
    public void run() {
        try {
            BufferedReader socketIn = mSocket.socketIn;
            while(true) {
                String s = socketIn.readLine();
                if (s.equals("stop")) {
                    break;
                }
                if (s != null) {
                    System.out.println(s);

                    Date time = new Date();
                    broader.broad(time, mSocket.username, s);
                }
            }
            broader.delSocket(mSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}