package pl.sages.javadevpro.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RoomHistoryService {

    public static void saveMessageToArchive(String chatRoomName, String message) {
        String historyFilePath = getFilePath(chatRoomName);
        StringBuilder composeMessage = new StringBuilder();
        composeMessage
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s")))
                .append(";")
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

    private static String getFilePath(String chatRoomName) {
        return "files/" + chatRoomName.substring(1) + "/archive.txt";
    }

}


