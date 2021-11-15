package pl.sages.javadevpro;

import pl.sages.javadevpro.filelocker.FileReaderLocker;
import pl.sages.javadevpro.filelocker.FileWriterLocker;
import pl.sages.javadevpro.ftp.FTPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class ServerApp {

    public static void main(String[] args) throws IOException {

        int ftpPort = 8888;
        int chatPort = 8080;

        String systemFtpPort = System.getProperty("ftpPort");
        if(systemFtpPort != null) {
            int temp = Integer.parseInt(systemFtpPort);
            if (temp >= 80 && temp <= 9000) {
                ftpPort = temp;
            }
        }

        String systemChatPort = System.getProperty("chatPort");
        if(systemFtpPort != null) {
            int temp = Integer.parseInt(systemChatPort);
            if (temp >= 80 && temp <= 9000) {
                chatPort = temp;
            }
        }

        ServerSocket ftpServerSocket = new ServerSocket(ftpPort);
        FTPServer ftpServer = new FTPServer(ftpServerSocket);
        Thread thread = new Thread(ftpServer);
        thread.start();

        ServerSocket serverSocket = new ServerSocket(chatPort);
        Server server = new Server(serverSocket);
        Thread thread1 = new Thread(server);
        thread1.start();

        System.out.println("Chat Server is running on port " + chatPort);
        System.out.println("FTP Server is running on port  " + ftpPort);
        boolean serverIsRunning = true;
        while (serverIsRunning) {
            Scanner scanner = new Scanner(System.in);

                String command = scanner.nextLine();
                String[] commandParts = command.split(" ", 2);

                switch (commandParts[0]) {
                    case "/readerLock":
                        FileReaderLocker fileReaderLocker = new FileReaderLocker(commandParts[1]);
                        Thread thread2 = new Thread(fileReaderLocker);
                        thread2.start();
                        break;
                    case "/writerLock":
                        FileWriterLocker fileWriterLocker = new FileWriterLocker(commandParts[1]);
                        Thread thread3 = new Thread(fileWriterLocker);
                        thread3.start();
                        break;
                    case "/quit":
                        server.closeServerSocket();
                        ftpServer.closeServerSocket();
                        serverIsRunning = false;
                        break;
                    case "/commands":
                        showListOfAvailableCommands();
                        break;
                    default:
                        showInformationAboutIncorrectCommand();
                }
        }
        System.out.println("Server has been closed.");
    }

    private static void showInformationAboutIncorrectCommand() {
        System.out.println("It looks like you wanted to invoke a command");
        System.out.println("to display a list of available commands, use the command: /commands");
    }

    private static void showListOfAvailableCommands() {
        System.out.println("List of available commands:");
        System.out.println("\t/quit - closes application");
        System.out.println("\t/readerLock <filename> - test only!!!");
        System.out.println("\t/writerLock <filename> - test only!!!");
    }
}
