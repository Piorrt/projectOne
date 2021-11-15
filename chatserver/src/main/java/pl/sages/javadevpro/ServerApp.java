package pl.sages.javadevpro;

import pl.sages.javadevpro.ftp.FTPServer;
import pl.sages.javadevpro.utils.Server;

import java.io.IOException;
import java.util.Scanner;

import static pl.sages.javadevpro.utils.Utils.createAndRunServer;
import static pl.sages.javadevpro.utils.Utils.readPortFromPropertyOrReturnDefault;

public class ServerApp {

    public static void main(String[] args) throws IOException {

        int ftpPort = readPortFromPropertyOrReturnDefault("ftpPort", 8888, 80, 9000);
        int chatPort = readPortFromPropertyOrReturnDefault("chatPort", 8080, 80, 9000);

        Server ftpServer = createAndRunServer(FTPServer.class, ftpPort);
        Server chatServer = createAndRunServer(ChatServer.class, chatPort);

        System.out.println("Chat Server is running on port " + chatPort);
        System.out.println("FTP Server is running on port  " + ftpPort);
        boolean serverIsRunning = true;
        while (serverIsRunning) {
            Scanner scanner = new Scanner(System.in);

                String command = scanner.nextLine();

                switch (command) {
                    case "/quit":
                        chatServer.closeServerSocket();
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
    }
}
