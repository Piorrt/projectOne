package pl.sages.javadevpro.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomsController {

    private final List<ChatRoom> allRooms = new ArrayList<>();

    public void moveUserToDifferentRoom(ChatUser user, String newRoomName) {
        ChatRoom oldChatRoom = user.getRoom();
        ChatRoom newChatRoom = allRooms.stream()
                .filter(r -> r.getChatRoomName().equals(newRoomName))
                .findFirst()
                .orElseGet(() -> createNewRoom(newRoomName));

        oldChatRoom.removeChatUser(user);
        newChatRoom.addChatUser(user);
    }

    public ChatRoom createNewRoom(String roomName) {
        ChatRoom newRoom = new ChatRoom(roomName);
        allRooms.add(newRoom);
        return newRoom;
    }


}
