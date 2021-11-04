package pl.sages.javadevpro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageHistoryManager {

    private ConcurrentLinkedQueue<String> chatRoomMessageHistory;
    //TODO refactor message from String to custom object (fields: fromUser, publishedInRoom, timestamp, messageBody)
    private ChatRoom room;
    private static final int MAX_ROOM_HISTORY_QUEUE_SIZE = 10;

    public MessageHistoryManager(ChatRoom room) {
        this.room = room;
        this.chatRoomMessageHistory = getRecentChatRoomHistoryFromArchive();
    }

    public void saveMessage(String message, String fromUser){
        saveMessageToChatRoomHistory(message,fromUser);
        saveMessageToArchive(message,fromUser);
    }

    private void saveMessageToChatRoomHistory(String message, String fromUser) {
        new Thread(() -> {
            chatRoomMessageHistory.removeIf(x -> chatRoomMessageHistory.size()>= MAX_ROOM_HISTORY_QUEUE_SIZE);
            chatRoomMessageHistory.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s")) + " " + fromUser+ ": " + message);
        }).start();
    }

    private void saveMessageToArchive(String message,String fromUser) {
        //TODO save message to file
    }

    private ConcurrentLinkedQueue<String> getRecentChatRoomHistoryFromArchive(){
        //TODO retrieve last X messages posted in this room
        return new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<String> getChatRoomMessageHistory() {
        return chatRoomMessageHistory;
    }
}
