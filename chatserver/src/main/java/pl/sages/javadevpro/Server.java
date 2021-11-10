package pl.sages.javadevpro;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            case "/priv": { handlePrivateCommand(user, commandParts[1], commandParts); break; }
            case "/files": { handleFilesCommand(user); break; }

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

        if (usernames.size() < 2) {
            user.writeMessage("-- You are the only user in this room --", "Room info");
            return;
        }

        usernames.stream()
                .filter(username -> !user.getUserName().equals(username))
                .forEach(username -> user.writeMessage("-- User: " + username + " is available in the room --", "Room info"));
    }

    public void handleFilesCommand(ChatUser user) {
        String dirname = user.getRoom().getChatRoomName().substring(1);
        File targetDir = new File("files", dirname);
        File[] files = targetDir.listFiles();
        Boolean thereAreNoFilesInThisRoom = true;

        for (File file : files) {
            if (file.isFile()) {
                user.writeMessage("-- File: " + file.getName() + " is available in the room --", "Room info");
                thereAreNoFilesInThisRoom = false;
            }
        }

        if (thereAreNoFilesInThisRoom.equals(true)) {
            user.writeMessage("-- There are no files in this room --", "Room info");
        }
    }

    public void handlePrivateCommand(ChatUser user, String toUser, String[] msg) {
        String message = IntStream
            .range(0, msg.length)
            .filter(i -> i > 1)
            .mapToObj(i -> msg[i])
            .collect(Collectors.joining(" "));

        if (!user.getRoom().sendMessageToUser(user.getUserName(), toUser, message)) {
            user.writeMessage("User: " + toUser + " is offline", "CHATROOM");
        }
    }
}