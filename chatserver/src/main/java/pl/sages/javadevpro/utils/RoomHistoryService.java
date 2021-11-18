package pl.sages.javadevpro.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RoomHistoryService {

    public static void saveMessageToArchive(String chatRoomName, String message) {
        Path historyFilePath = getHistoryFilePath(getFilePath(chatRoomName));
        StringBuilder composeMessage = new StringBuilder();
        composeMessage
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s")))
                .append(";")
                .append(message)
                .append("\n");
        try {
            Files.writeString(historyFilePath,
                    composeMessage.toString(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFilePath(String chatRoomName) {
        return "files/" + chatRoomName.substring(1) + "/archive.txt";
    }

    private static Path getHistoryFilePath(String historyFilePath){
        Path filePath = Path.of(historyFilePath);
        if (Files.notExists(filePath)) {
            try {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

}


