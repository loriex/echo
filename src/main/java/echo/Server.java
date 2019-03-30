package echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(23333);
            System.out.println("Listening on port: "+serverSocket.getLocalPort());
            try {
                while (true) { 
                    Socket socket = new Socket();
                    socket = serverSocket.accept();
                    
                    System.out.println("accept connection from: "+socket.getInetAddress().getHostAddress());
                    
                    Echor echor = new Echor(socket);
                    echor.start();
                }
            } catch(IOException e) {
                e.printStackTrace();
                System.err.println("Error");
            }
            serverSocket.close();
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Error");
        }
    }
}
class Echor extends Thread {
    Socket socket;
    Echor(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
            while(true) {
                String s = socketIn.readLine();
                if (s != null) {
                    System.out.println(s);

                    Date time = new Date();
                    // socketOut.println("Server "+time.getTime());
                    socketOut.println(s);
                    socketOut.flush();
                }
                if (s.equals("stop")) {
                    break;
                }
            }
            // socketIn.close();
            // socketOut.close();
            // socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}