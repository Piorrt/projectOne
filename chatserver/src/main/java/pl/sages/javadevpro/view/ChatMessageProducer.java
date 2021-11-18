package pl.sages.javadevpro.view;

import pl.sages.javadevpro.chat.ChatUser;

import java.io.File;
import java.util.List;

public class ChatMessageProducer {

    public String userMessage(String messageBody, ChatUser sender) {
        return mainMessageComposition(sender.getRoom().getChatRoomName(),sender.getUserName(),messageBody);
    }

    public String privateUserMessage(String message, ChatUser sender) {
        return mainMessageComposition(sender.getRoom().getChatRoomName(),"Private message from " + sender, message);
    }

    public String userJoinChatRoomInfo(String chatName, String userName) {
        return mainMessageComposition(chatName,"Room info","User " + userName + " joined chat room.");
    }

    public String userLeftChatRoomInfo(String chatName, String userName) {
        return mainMessageComposition(chatName,"Room info","User " + userName + " left chat room.");
    }

    public String userIsOfflineInfo(String roomName, String userName){
        return mainMessageComposition(roomName,"Room info", "User " + userName + " is offline");
    }

    public String listOfChatRoomMembersInfo(String chatName, List<String> members){
        StringBuilder stringBuilder = new StringBuilder();
        if (members.size()<2){
            stringBuilder.append("-- You are the only user in this room. --");
        } else {
            members.forEach(username -> stringBuilder
                    .append("'\n-- User: ")
                    .append(username)
                    .append(" is available in the room --"));
        }
        return mainMessageComposition(chatName, "Room info", stringBuilder.toString());
    }

    public String listOfFilesAvailableInTheRoomInfo(String chatName, File[] files) {
        boolean thereAreNoFilesInThisRoom = true;
        StringBuilder stringBuilder = new StringBuilder();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    stringBuilder
                            .append("'\n-- File: ")
                            .append(file.getName())
                            .append(" is available in the room --");
                    thereAreNoFilesInThisRoom = false;
                }
            }
        }
        if (thereAreNoFilesInThisRoom) {
            stringBuilder.append("-- There are no files in this room --");
        }
        return mainMessageComposition(chatName,"Room info",stringBuilder.toString());
    }

    public String commandNotSupportedInfo() {
        return mainMessageComposition("SERVER","Server info","Command not supported");
    }

    private String mainMessageComposition(String roomName, String senderName, String message) {
        return new StringBuilder()
                .append(roomName)
                .append("->")
                .append(senderName)
                .append(": ")
                .append(message)
                .toString();
    }

}
