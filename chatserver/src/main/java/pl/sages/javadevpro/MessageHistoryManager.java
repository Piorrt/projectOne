package pl.sages.javadevpro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class MessageHistoryManager {

    private ConcurrentLinkedQueue<String> chatRoomMessageHistory;
    private String chatRoomName;
    private static final int MAX_ROOM_HISTORY_QUEUE_SIZE = 10;
    private final String HISTORY_ARCHIVE_FILE = "chat_history.txt";


    public MessageHistoryManager(String chatRoomName) {
        this.chatRoomName = chatRoomName;
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
        String composedMessage = chatRoomName
                +";"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s"))
                + ";" + fromUser
                + ": " + message
                + "\n";
        try {
            Files.writeString(Path.of(HISTORY_ARCHIVE_FILE),
                    composedMessage,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ConcurrentLinkedQueue<String> getRecentChatRoomHistoryFromArchive() {
        ConcurrentLinkedQueue<String> recentChatHistory = new ConcurrentLinkedQueue<>();
        try {
            if (!Files.exists(Path.of(HISTORY_ARCHIVE_FILE))){
                Files.createFile(Path.of(HISTORY_ARCHIVE_FILE));
            }

            List<String> filteredHistory = Files.readAllLines(Path.of(HISTORY_ARCHIVE_FILE)).stream()
                    .filter((line) -> chatRoomName.equals(line.split(";")[0])).collect(Collectors.toList());

            filteredHistory.stream().skip(filteredHistory.size()>MAX_ROOM_HISTORY_QUEUE_SIZE ? filteredHistory.size()-MAX_ROOM_HISTORY_QUEUE_SIZE : 0)
                    .map((line)->line.replace(";"," "))
                    .forEach(recentChatHistory::add);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return recentChatHistory;
    }

    public ConcurrentLinkedQueue<String> getChatRoomMessageHistory() {
        return chatRoomMessageHistory;
    }

}


