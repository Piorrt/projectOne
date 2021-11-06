package pl.sages.javadevpro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;
    private volatile boolean isConnected;


    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.isConnected = true;
            this.userName = userName;
        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    public void sendMessage() {
        try {
            writer.write(userName);
            writer.newLine();
            writer.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                writer.write(messageToSend);
                writer.newLine();
                writer.flush();
                if (handleQuitCommand(messageToSend)){ return; }
            }
        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while(isConnected) {
                    try {
                        msgFromGroupChat = reader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeAll(socket, reader, writer);
                    }
                }
            }
        }).start();
    }

    public boolean handleQuitCommand(String quitCommand) {
        if(quitCommand.equals("/quit")) {
            this.isConnected = false;
            closeAll(this.socket, this.reader, this.writer);
            System.out.println("You has disconnected from server.");
            return true;
        }
        return false;
    }

    public void closeAll(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if(reader != null) {
                reader.close();
            }
            if(writer != null) {
                writer.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
