package pl.sages.javadevpro.chat;

import pl.sages.javadevpro.view.InternalServerInfoPrinter;
import pl.sages.javadevpro.view.ChatMessageProducer;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserCommandsHandler {

    private static final ChatRoomsController roomsController = new ChatRoomsController();
    private final ChatMessageProducer messageProducer;
    private final ChatUser user;

    public UserCommandsHandler(ChatUser user) {
        this.user = user;
        messageProducer = new ChatMessageProducer();
    }

    public void dispatchCommand(String command) {
        String[] commandParts = command.split(" ");
        switch (commandParts[0]) {
            case "/join": { handleJoinCommand(commandParts[1], user); break; }
            case "/exit": { handleExitCommand(user); break; }
            case "/quit": { handleQuitCommand(user); break; }
            case "/list": { handleListCommand(user); break; }
            case "/priv": { handlePrivateCommand(user, commandParts[1], commandParts); break; }
            case "/files": { handleFilesCommand(user); break; }

            default: user.writeToClient(messageProducer.commandNotSupportedInfo());
        }
    }

    public void handleJoinCommand(String chatName, ChatUser user) {
        roomsController.moveUserToDifferentRoom(user,chatName);
    }

    public void handleExitCommand(ChatUser user) {
        roomsController.moveUserToDifferentRoom(user, "#general");
    }

    public void handleQuitCommand(ChatUser user) {
        user.getRoom().removeChatUser(user);
        user.closeUser();
        InternalServerInfoPrinter.printClientDisconnectedInfo(user.getUserName());
    }

    public void handleListCommand(ChatUser user) {
        ChatRoom usersRoom = user.getRoom();
        List<String> usernames = usersRoom.getAllUsernames();
        String commandResult = messageProducer.listOfChatRoomMembersInfo(usersRoom.getChatRoomName(),usernames);
        user.writeToClient(commandResult);
    }

    public void handleFilesCommand(ChatUser user) {
        String usersRoomName =  user.getRoom().getChatRoomName();
        File targetDir = new File("files", usersRoomName.substring(1));
        File[] files = targetDir.listFiles(); //targetDir.listFiles() == null ? new File[0] : targetDir.listFiles();
        String commandResult = messageProducer.listOfFilesAvailableInTheRoomInfo(usersRoomName,files);
        user.writeToClient(commandResult);
    }

    public void handlePrivateCommand(ChatUser user, String toUser, String[] msg) {
        ChatRoom room = user.getRoom();
        String resultMessage;
        if (room.getAllUsernames().contains(toUser)) {
            String messageBody = IntStream
                    .range(0, msg.length)
                    .filter(i -> i > 1)
                    .mapToObj(i -> msg[i])
                    .collect(Collectors.joining(" "));
            resultMessage = messageProducer.privateUserMessage(messageBody,user);
            room.sendMessageToUser(resultMessage,toUser);
        }
        else {
            resultMessage = messageProducer.userIsOfflineInfo(user.getRoom().getChatRoomName(),toUser);
            user.writeToClient(resultMessage);
        }
    }
}
