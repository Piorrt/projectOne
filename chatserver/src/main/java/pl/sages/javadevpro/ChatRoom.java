package pl.sages.javadevpro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatRoom {

    private List<ChatUser> users = new ArrayList<>();
    private String chatRoomName;
    private ConcurrentLinkedQueue<String> messageHistory = new ConcurrentLinkedQueue<>();


    public ChatRoom(String name) {
        this.chatRoomName = name;
    }

    public void addChatUser(ChatUser chatUser) {
        sendMessageToAllUsers("-- User: " + chatUser.getUserName() + " connected to chat room --", null);
        users.add(chatUser);
        messageHistory.forEach(message ->  chatUser.writeMessage(message,null));
    }

    public void removeChatUser(ChatUser chatUser) {
        sendMessageToAllUsers("-- User: " + chatUser.getUserName() + " left chat room --", null);
        users.remove(chatUser);
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void sendMessageToAllUsers(String message, String fromUser) {
        users.stream()
                .filter(chatUser -> !chatUser.getUserName().equals(fromUser))
                .forEach(chatUser -> chatUser.writeMessage(message, fromUser));
        addMessageToHistory(message);
    }

    public void addMessageToHistory(String message) {
        messageHistory.removeIf(x -> messageHistory.size()>=10);
        messageHistory.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s")) + message);
    }

}
