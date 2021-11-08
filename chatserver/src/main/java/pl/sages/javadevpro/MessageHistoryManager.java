package pl.sages.javadevpro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageHistoryManager {

    private ConcurrentLinkedQueue<String> chatRoomMessageHistory;
    private ChatRoom room;
    private static final int MAX_ROOM_HISTORY_QUEUE_SIZE = 10;
    private final String HISTORY_FILE = "chat_history";


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
        String composedMessage = room.getChatRoomName()
                +";"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s"))
                + ";" + fromUser
                + ": " + message
                + "\n";
        try {
            Files.writeString(Path.of(HISTORY_FILE),
                    composedMessage,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ConcurrentLinkedQueue<String> getRecentChatRoomHistoryFromArchive(){
        //TODO retrieve last X messages posted in this room
        return new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<String> getChatRoomMessageHistory() {
        return chatRoomMessageHistory;
    }

}


