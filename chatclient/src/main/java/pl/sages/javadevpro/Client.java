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
    private boolean connectionIsOpen;


    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
            this.connectionIsOpen = true;
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
            while (connectionIsOpen) {
                String messageToSend = scanner.nextLine();
                String[] messageParts = messageToSend.split(" ");
                switch (messageParts[0]) {
                    case "/quit": { handleQuitCommand(); break; }
                    default:
                        writer.write(messageToSend);
                        writer.newLine();
                        writer.flush();
                }
            }
        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    private void handleQuitCommand() {
        try {
            writer.write("/quit");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println("Could not send quit information to the server. Closing the chat anyway.");
        }
        finally {
            closeAll(socket,reader,writer);
            this.connectionIsOpen = false;
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while(connectionIsOpen) {
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
