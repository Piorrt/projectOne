package pl.sages.javadevpro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ChatRoom {

    private List<ChatUser> users = new ArrayList<>();
    private String chatRoomName;
    private MessageHistoryManager messageHistoryManager;


    public ChatRoom(String name) {
        this.chatRoomName = name;
        this.messageHistoryManager = new MessageHistoryManager(name);
    }

    public void addChatUser(ChatUser chatUser) {
        sendMessageToAllUsers("-- User " + chatUser.getUserName() + " connected to chat room --", "Room info");
        users.add(chatUser);
        messageHistoryManager.getLastXMessages(10).forEach(message ->  chatUser.writeMessage(message,""));
    }

    public void removeChatUser(ChatUser chatUser) {
        sendMessageToAllUsers("-- User " + chatUser.getUserName() + " left chat room --", "Room info");
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

    public boolean sendMessageToUser(String fromUser, String toUser, String msg) {
        Optional<ChatUser> chatUser = users.stream()
            .filter(user -> user.getUserName().equals(toUser))
            .findFirst();
        if (chatUser.isPresent()) {
            chatUser.get().writeMessage("Private message from " + fromUser + "-> " + msg, fromUser);
            return true;
        }
        return false;
    }
}
