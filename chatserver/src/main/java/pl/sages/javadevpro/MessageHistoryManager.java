package pl.sages.javadevpro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageHistoryManager {

    private List<String> chatRoomMessageHistory;
    private String historyFilePath;
    private String chatRoomName;


    public MessageHistoryManager(String chatRoomName) {
        this.historyFilePath = "history/" + chatRoomName.substring(1) + "/archive.txt";
        this.chatRoomMessageHistory = getHistoryFromArchive();
        this.chatRoomName = chatRoomName;
    }

    public void saveMessage(String message, String fromUser){
        saveMessageToChatRoomHistory(message,fromUser);
        saveMessageToArchive(message,fromUser);
    }

    private void saveMessageToChatRoomHistory(String message, String fromUser) {
        chatRoomMessageHistory.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s")) + " " + fromUser+ ": " + message);
    }

    private void saveMessageToArchive(String message,String fromUser) {
        StringBuilder composeMessage = new StringBuilder();
        composeMessage
                .append(chatRoomName)
                .append(";")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s")))
                .append(";")
                .append(fromUser)
                .append(":")
                .append(message)
                .append("\n");

        try {
            Files.writeString(Path.of(historyFilePath),
                    composeMessage.toString(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getHistoryFromArchive() {
        try {
            if (Files.notExists(Path.of(historyFilePath))) {
                Files.createDirectories(Path.of(historyFilePath).getParent());
                Files.createFile(Path.of(historyFilePath));
            }
            return Files.readAllLines(Path.of(historyFilePath));
        }
        catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getLastXMessages(int x) {
        return chatRoomMessageHistory.stream()
                .skip(chatRoomMessageHistory.size()>x ? chatRoomMessageHistory.size()-x : 0)
                .collect(Collectors.toList());
    }

}


