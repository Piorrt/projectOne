package pl.sages.javadevpro;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

    private List<ChatUser> users = new ArrayList<>();
    private String chatRoomName;
    private MessageHistoryManager messageHistoryManager = new MessageHistoryManager(this);


    public ChatRoom(String name) {
        this.chatRoomName = name;
    }

    public void addChatUser(ChatUser chatUser) {
        sendMessageToAllUsers("-- User: " + chatUser.getUserName() + " connected to chat room --", null);
        users.add(chatUser);
        messageHistoryManager.getChatRoomMessageHistory().forEach(message ->  chatUser.writeMessage(message,null));
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
        messageHistoryManager.saveMessage(message,fromUser);
    }

    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();

        users.stream()
                .forEach(chatUser -> usernames.add(chatUser.getUserName()));
        return usernames;
    }
}
