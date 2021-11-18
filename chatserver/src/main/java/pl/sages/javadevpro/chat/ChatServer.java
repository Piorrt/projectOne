package pl.sages.javadevpro.chat;

import pl.sages.javadevpro.utils.Server;
import pl.sages.javadevpro.view.InternalServerInfoPrinter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements Server {

    private static final int MAX_CHAT_USERS = 10;

    private final ServerSocket serverSocket;
    private static final ChatRoomsController roomsController = new ChatRoomsController();

    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        ChatRoom generalRoom = roomsController.createNewRoom("#general");
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CHAT_USERS);

        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                executorService.execute(createNewUser(socket,generalRoom));
            } catch (IOException e) {
                closeServerSocket();
            }
        }
    }

    @Override
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable createNewUser(Socket clientSocket, ChatRoom initialChatRoom) {
        return () -> {
            InternalServerInfoPrinter.printNewClientConnectedInfo();
            ChatUser chatUser = new ChatUser(clientSocket);
            initialChatRoom.addChatUser(chatUser);
            chatUser.listenToClient();
        };
    }

}