package pl.sages.javadevpro;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

    private List<ChatUser> users = new ArrayList<>();
    private String chatRoomName;

    ///TODO HISTORY

    public ChatRoom(String name) {
        this.chatRoomName = name;
    }

    public void addChatUser(ChatUser chatUser) {
        sendMessageToAllUsers("-- User: " + chatUser.getUserName() + " connected to chat room --", null);
        users.add(chatUser);
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
    }
}
