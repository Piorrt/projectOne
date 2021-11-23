package pl.sages.javadevpro;

import pl.sages.javadevpro.chat.ChatServer;
import pl.sages.javadevpro.ftp.FTPServer;
import pl.sages.javadevpro.utils.Server;
import pl.sages.javadevpro.view.InternalServerInfoPrinter;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static pl.sages.javadevpro.utils.Utils.createServer;
import static pl.sages.javadevpro.utils.Utils.readPortFromPropertyOrReturnDefault;

public class ServerApp {

    public static void main(String[] args) throws IOException {

        int ftpPort = readPortFromPropertyOrReturnDefault("ftpPort", 8888);
        int chatPort = readPortFromPropertyOrReturnDefault("chatPort", 8080);
        Server ftpServer = createServer(FTPServer.class, ftpPort);
        Server chatServer = createServer(ChatServer.class, chatPort);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(ftpServer);
        executorService.execute(chatServer);

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
                    InternalServerInfoPrinter.printListOfAvailableCommands();
                    break;
                default:
                    InternalServerInfoPrinter.printInformationAboutIncorrectCommand();
            }
        }
        InternalServerInfoPrinter.printServerClosingInfo();
    }


}
