package pl.sages.javadevpro;

import pl.sages.javadevpro.ftp.FTPDownloader;
import pl.sages.javadevpro.ftp.FTPSender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;
    private volatile boolean isConnected;
    private String roomName;


    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.isConnected = true;
            this.userName = userName;
            this.roomName = "general";

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
                String[] messageParts = messageToSend.split(" ", 2);

                if (isCorrectInput(messageToSend)) {

                    switch (messageParts[0]) {
                        case "/send":
                            handleSendFileCommand(messageParts[1]);
                            break;
                        case "/download":
                            handleDownloadFileCommand(messageParts[1]);
                            break;
                        case "/join":
                            this.roomName = messageParts[1].substring(1);
                            handleSendMessage(messageToSend);
                            break;
                        case "/exit":
                            this.roomName = "#general".substring(1);
                            handleSendMessage(messageToSend);
                            break;
                        case "/quit":
                            handleQuitCommand();
                            return;
                        default:
                            handleSendMessage(messageToSend);
                    }
                }

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

                while (isConnected) {
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

    public void handleSendMessage(String messageToSend) throws IOException {
        writer.write(messageToSend);
        writer.newLine();
        writer.flush();
    }

    public void handleSendFileCommand(String fileName) throws IOException {
        Socket ftpSocket = new Socket("localhost", 8888);
        FTPSender ftpSender = new FTPSender(ftpSocket, fileName, roomName);
        Thread thread = new Thread(ftpSender);
        thread.start();
    }

    public void handleDownloadFileCommand(String fileName) throws IOException {
        Socket ftpSocket = new Socket("localhost", 8888);
        FTPDownloader ftpDownloader = new FTPDownloader(ftpSocket, fileName, roomName);
        Thread thread = new Thread(ftpDownloader);
        thread.start();
    }

    public void handleQuitCommand() throws IOException {
        handleSendMessage("/quit");
        this.isConnected = false;
        closeAll(this.socket, this.reader, this.writer);
        System.out.println("You have disconnected from server.");
    }

    public void closeAll(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isCorrectInput(String inputText) {
        if (inputText.startsWith("/")) {
            return isCorrectCommand(inputText);
        }
        return true;
    }

    public boolean isCorrectCommand(String inputText) {
        String[] commandParts = inputText.split(" ");
        switch (commandParts[0]) {
            case "/join": {
                return true;
            }
            case "/exit": {
                return true;
            }
            case "/list": {
                return true;
            }
            case "/send": {
                return true;
            }
            case "/download": {
                return true;
            }
            case "/files": {
                return true;
            }
            case "/quit": {
                return true;
            }
            case "/priv": {
                return true;
            }
            default: {
                System.out.println("Command not supported");
                return false;
            }
        }
    }
}
