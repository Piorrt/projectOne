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
    private int ftpPort;


    public Client(Socket socket, String userName, int ftpPort) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.isConnected = true;
            this.userName = userName;
            this.roomName = "general";
            this.ftpPort = ftpPort;

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

//                if (isCorrectInput(messageToSend)) {

                    switch (messageParts[0]) {
                        case "/send":
                            handleSendFileCommand(messageParts[1]);
                            break;
                        case "/download":
                            handleDownloadFileCommand(messageParts[1]);
                            break;
                        case "/join":
                            //checking the correctness of the command
                            if (messageParts.length == 2
                                    && messageParts[1].startsWith("#")
                                    && !messageParts[1].contains(" ")) {
                                this.roomName = messageParts[1].substring(1);
                                handleSendMessage(messageToSend);
                            } else {
                                showInformationAboutIncorrectCommand();
                            }
                            break;
                        case "/exit":
                            this.roomName = "#general".substring(1);
                            handleSendMessage(messageToSend);
                            break;
                        case "/quit":
                            handleSendMessage(messageToSend);
                            handleQuitCommand();
                            return;
                        case "/commands":
                            showListOfAvailableCommands();
                            break;
                        case "/files":
                            handleSendMessage(messageToSend);
                            break;
                        case "/list":
                            handleSendMessage(messageToSend);
                            break;
                        default:
                            if (messageToSend.startsWith("/")) {
                                showInformationAboutIncorrectCommand();
                                break;
                            }
                            handleSendMessage(messageToSend);
                    }
//                }

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

    public void showInformationAboutIncorrectCommand() {
        System.out.println("It looks like you wanted to invoke a command");
        System.out.println("to display a list of available commands, use the command: /commands");
    }
    public void showListOfAvailableCommands() {
        System.out.println("List of available commands:");
        System.out.println("\t/send <filename> - sends the <filename> file to the room on the server");
        System.out.println("\t/download <filename> - downloads the <filename> file from the room on the server");
        System.out.println("\t/files - displays the files available in the room on the server");
        System.out.println("\t/join <#roomname> - takes the user from the general room to room <#roomname>");
        System.out.println("\t/exit - takes the user to the general room");
        System.out.println("\t/list - displays a list of users who are in the room");
        System.out.println("\t/quit - closes application");
    }

    public void handleSendFileCommand(String fileName) throws IOException {
        Socket ftpSocket = new Socket("localhost", ftpPort);
        FTPSender ftpSender = new FTPSender(ftpSocket, fileName, roomName);
        Thread thread = new Thread(ftpSender);
        thread.start();
    }

    public void handleDownloadFileCommand(String fileName) throws IOException {
        Socket ftpSocket = new Socket("localhost", ftpPort);
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
            case "/commands": {
                return true;
            }
            default: {
                System.out.println("Command not supported");
                return false;
            }
        }
    }
}
