package pl.sages.javadevpro;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

    private List<UserChat> users = new ArrayList<>();
    private String chatRoomName;

    ///TOD HISTORY
    public ChatRoom(String name) {
        this.chatRoomName = name;
    }

    public void addChatUser(UserChat chatUser) {
        users.add(chatUser);
    }

    public void removeChatUser(UserChat chatUser) {
        users.remove(chatUser);
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void sendMessageToAllUsers(String message, String fromUser) {
        users.stream()
            .filter(userChat -> !userChat.getUserName().equals(fromUser))
            .forEach(userChat -> userChat.writeMessage(message, fromUser));
    }
}
