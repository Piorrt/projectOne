package pl.sages.javadevpro;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static pl.sages.javadevpro.utils.Utils.readPortFromPropertyOrReturnDefault;

public class ClientApp {

    public static void main(String[] args) throws IOException {

        int ftpPort = readPortFromPropertyOrReturnDefault("ftpPort", 8888, 80, 9000);
        int chatPort = readPortFromPropertyOrReturnDefault("chatPort", 8080, 80, 9000);

        System.out.println("Chat Client is running on port " + chatPort);
        System.out.println("FTP Client is running on port  " + ftpPort);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your user name for the group chat: ");
        String userName = scanner.nextLine();
        Socket socket = new Socket("localhost", chatPort);
        Client client = new Client(socket, userName, ftpPort);

        client.listenForMessages();
        client.sendMessage();

    }
}
