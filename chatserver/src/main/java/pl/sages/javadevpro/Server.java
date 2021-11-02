package pl.sages.javadevpro;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Server {

    private ServerSocket serverSocket;
    private List<ChatUser> users = new ArrayList<>();
    private List<ChatRoom> rooms = new ArrayList<>();

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() {
        ChatRoom generalChatRoom = new ChatRoom("#general");
        rooms.add(generalChatRoom);
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New client has connected.");

                ChatUser chatUser = new ChatUser(this, generalChatRoom, socket);
                generalChatRoom.addChatUser(chatUser);
                users.add(chatUser);

                Thread thread = new Thread(chatUser);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commandsHandle(String command, ChatUser user) {
        String[] commandParts = command.split(" ");
        switch (commandParts[0]) {
            case "/join": { handleJoinCommand(commandParts[1], user); break; }
            default:
                System.out.println("Command not supported");
        }
    }

    public void handleJoinCommand(String chatName, ChatUser user) {
        user.getRoom().removeChatUser(user);
        ChatRoom chatRoom = rooms.stream()
            .filter(r -> r.getChatRoomName().equals(chatName))
            .findFirst().orElseGet(() -> {
                ChatRoom room = new ChatRoom(chatName);
                rooms.add(room);
                return room;
            });
        chatRoom.addChatUser(user);
        user.setRoom(chatRoom);
    }
}