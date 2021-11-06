package pl.sages.javadevpro;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            case "/exit": { handleExitCommand(user); break; }
            case "/quit": { handleQuitCommand(user); break; }
            case "/list": { handleListCommand(user); break; }
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

    public void handleExitCommand(ChatUser user) {
        String chatName = user.getRoom().getChatRoomName();
        final String mainChatName = "#general";

        if(chatName.equals(mainChatName))
            return;

        user.getRoom().removeChatUser(user);

        ChatRoom mainChatRoom = rooms.stream()
                .filter(r -> r.getChatRoomName().equals(mainChatName))
                .findFirst().get();

        mainChatRoom.addChatUser(user);
        user.setRoom(mainChatRoom);
    }
    public void handleQuitCommand(ChatUser user) {
        user.getRoom().removeChatUser(user);
        user.closeUser();
        users.remove(user);
        System.out.println("Client " + user.getUserName() + " has disconnected.");
    }

    public void handleListCommand(ChatUser user) {
        List<String> usernames = user.getRoom().getAllUsernames();
        usernames.stream()
                .filter(username -> !user.getUserName().equals(username))
                .forEach(username -> user.writeMessage("-- User: " + username + " is available in the room --", "Room info"));
    }
}