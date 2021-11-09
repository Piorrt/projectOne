package pl.sages.javadevpro;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {

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
