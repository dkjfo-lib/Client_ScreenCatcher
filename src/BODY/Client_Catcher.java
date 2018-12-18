package BODY;

import HEAD.monitorWindow;

import javax.imageio.ImageIO;

import static Files.FileWork.readFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client_Catcher {

    static final int PORT = 8080;
    public static BufferedImage lastReceivedMessage;

    public static void main(String[] args) {
        try {
            sendMessageToHost();
        } catch (IOException ioe) {
            System.err.println("Could not read from file : " + ioe);
        }
        try {

            // configure listening socket
            String myLocalAddress = InetAddress.getLocalHost().getHostAddress();
            InetSocketAddress currAddress = new InetSocketAddress(myLocalAddress, PORT);
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(currAddress);

            Thread captureUpdate = new Thread(() -> {
                try {
                        // listen for HOST's message
                        System.out.println(Thread.currentThread().getName() + " is listening for the message on IP : " + myLocalAddress + ":" + PORT + "...");
                        Socket incomingSocket = serverSocket.accept();
                        InputStream in = incomingSocket.getInputStream();
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + " is listening for a new message...");

                        lastReceivedMessage = ImageIO.read(in);

                        synchronized (serverSocket) {
                            serverSocket.notifyAll();
                        }
                    }
                } catch (IOException ioe) {
                    System.err.println("Server fell!!! : " + ioe);
                }
            });

            Thread drawImage = new Thread(()-> {
                monitorWindow window = new monitorWindow();
                while (true) {
                    synchronized (serverSocket) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " is going to sleep...");
                            serverSocket.wait();
                        } catch (InterruptedException ie) {
                            System.out.println(Thread.currentThread().getName() + " is going out...");
                        }
                    }
                    window.run();
                }
            });

            captureUpdate.start();
            drawImage.start();


        } catch (IOException ioe) {
            System.err.println("Exception during server work : " + ioe);
        }
    }


    static void sendMessageToHost() throws IOException {
        // get head server ip and port
        String[] headLocalIP = readFile();
        System.out.println("HOST Server IP : " + headLocalIP[0] + ":" + headLocalIP[1]);

        // configure socket for server
        Socket socket = new Socket(headLocalIP[0], Integer.parseInt(headLocalIP[1]));
        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // send request to server
        String myLocalAddress = InetAddress.getLocalHost().getHostAddress();
        serverWriter.write(myLocalAddress);

        // free resources
        serverWriter.flush();
        serverWriter.close();
        socket.close();
    }
}
