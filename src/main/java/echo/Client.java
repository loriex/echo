package echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 23333);
            System.out.println("...connected\n");

            //a thread that receive data from Server and print it out.
            Receiver receivier = new Receiver(socket);
            receivier.start();

            System.out.println("PLEASE INPUT YOUR NICKNAME AND PRESS ENTER.");
            //and here, we loop to get data from keyboard and send it to Server(until you type stop)
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String string = scanner.nextLine();
                if (string != null){
                    Date time = new Date();
                    // socketOut.println("Client "+time.getTime());
                    socketOut.println(string);
                    socketOut.flush();
                }
                if (string.equals("stop"))
                    break;
            }

            //clean resources.
            socketOut.close();
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Error");
        }
    }
}
class Receiver extends Thread {
    private final Socket s;
    Receiver(Socket s) {
        this.s = s;
    }
    @Override
    public void run() {
        try {
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while (true) {
                String s = socketIn.readLine();
                System.out.println(s);
                if (s.equals("stop")) {
                    break;
                }
            }
            socketIn.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}