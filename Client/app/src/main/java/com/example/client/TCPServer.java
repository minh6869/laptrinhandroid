package com.example.client;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private final int serverPort = 8080;
    private ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<ClientHandler> clients = new ArrayList<>();

    public void start() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            serverSocket = new ServerSocket(serverPort, 50, InetAddress.getByName(ip.getHostAddress()));
            System.out.println("Server started on " + ip.getHostAddress() + " port " + serverPort);
            System.out.println("Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                executorService.execute(clientHandler);
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            for (ClientHandler client : clients) {
                client.closeConnection();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
            executorService.shutdown();
            System.out.println("Server stopped");
        } catch (Exception e) {
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (Exception e) {
                System.out.println("Error setting up client handler: " + e.getMessage());
                closeConnection();
            }
        }

        @Override
        public void run() {
            try {
                // Send welcome message
                String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                writer.println("Welcome to the TCP Server! Connected at " + timestamp);

                // Handle incoming messages
                String message;
                while ((message = reader.readLine()) != null) {
                    String clientAddress = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("Message from " + clientAddress + ": " + message);

                    // Echo the message back with a timestamp
                    String response = "Echo: " + message + " (at " + getTimeStamp() + ")";
                    writer.println(response);
                }
            } catch (Exception e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                closeConnection();
                clients.remove(this);
                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
            }
        }

        public void closeConnection() {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (!clientSocket.isClosed()) clientSocket.close();
            } catch (Exception e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }

        private String getTimeStamp() {
            return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        }
    }

    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        server.start();
    }
}
