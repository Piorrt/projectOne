package pl.sages.javadevpro.chat;

import pl.sages.javadevpro.utils.RoomHistoryService;
import pl.sages.javadevpro.view.ChatMessageProducer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRoom {

    private final List<ChatUser> users = Collections.synchronizedList(new ArrayList<>());
    private final String chatRoomName;
    private final ChatMessageProducer messageProducer = new ChatMessageProducer();

    public ChatRoom(String name) {
        this.chatRoomName = name;
    }

    synchronized public void addChatUser(ChatUser chatUser) {
        users.add(chatUser);
        chatUser.setRoom(this);
        String resultMessage = messageProducer.userJoinChatRoomInfo(chatRoomName,chatUser.getUserName());
        sendMessageToAllUsers(resultMessage,chatUser);
    }

    synchronized public void removeChatUser(ChatUser chatUser) {
        users.remove(chatUser);
        String resultMessage = messageProducer.userLeftChatRoomInfo(chatRoomName,chatUser.getUserName());
        sendMessageToAllUsers(resultMessage,chatUser);
    }

    synchronized public void sendMessageToAllUsers(String message, ChatUser fromUser) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(()-> users.stream()
                .filter(chatUser -> !chatUser.equals(fromUser))
                .forEach(chatUser -> chatUser.writeToClient(message)));
        executorService.execute(()->RoomHistoryService.saveMessageToArchive(chatRoomName,message));
    }

    synchronized public void sendMessageToUser(String msg, String toUser) {
        Optional<ChatUser> chatUser = users.stream()
            .filter(user -> user.getUserName().equals(toUser))
            .findFirst();
        chatUser.ifPresent(user -> user.writeToClient(msg));
    }

    synchronized public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        users.forEach(chatUser -> usernames.add(chatUser.getUserName()));
        return usernames;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }



}
