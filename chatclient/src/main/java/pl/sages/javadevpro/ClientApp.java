package pl.sages.javadevpro;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your user name for the group chat: ");
        String userName = scanner.nextLine();
        Socket socket = new Socket("localhost", 8080);
        Client client = new Client(socket, userName);
        client.listenForMessages();
        client.sendMessage();
    }
}
