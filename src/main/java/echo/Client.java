package echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Client {
    private static String test;
    private static int count = 0;
    private static int sendLim = 0;
    private static Scanner scanner = new Scanner(System.in);
    static String getLine() {
        if (test.equals("PROD")) {
            return scanner.nextLine();
        } else {
            ++count;
            if (count >= sendLim) {
                return "stop";
            } else {
                return "MESSAGE_SEND_TIME " + System.nanoTime();
            }
        }
    }
    public static void main(String[] args) {
        // for (String s : args) {
        //     System.out.println(s);
        // }
        if (args.length > 0 && args[0].toUpperCase().equals("TEST"))
            test = "TEST";
        else
            test = "PROD";
        try {
            for (sendLim = 10; sendLim <= 100000; sendLim *= 10) {
                count = 0;
                Socket socket = new Socket("127.0.0.1", 23333);
                System.out.println("...connected\n");

                //a thread that receive data from Server and print it out.
                Receiver receivier = new Receiver(socket);
                receivier.start();

                PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
                    
                //and here, we loop to get data from keyboard and send it to Server(until you type stop)
                    while (true) {
                        String string = getLine();
                        if (string != null) {
                            socketOut.println(string);
                            socketOut.flush();
                        }
                        if (string.equals("stop"))
                            break;
                    }
                socketOut.flush();
                //clean resources.
                //close PrintWriter will lead to close socket(both in & out)
                //so shutdownOutput(it will only close the **output port** of **client**)
                socket.shutdownOutput();
            }
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
        List<Long> times = new ArrayList<>();
        try {
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while (true) {
                String s = socketIn.readLine();
                if (s.equals("stop")) {
                    break;
                }
                long time_s = Long.parseLong(s.split(" ")[1]);
                long time_r = System.nanoTime();
                times.add(time_r-time_s);
                // System.out.println("["+time_s+" , "+time_r+"]");
                // System.out.println("MESSAGE_RECV_TIME " + time_r);
                // System.out.println(s);

            }
            socketIn.close();
            s.close();
            Analysis.Generate(times);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}