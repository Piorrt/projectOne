package pl.sages.javadevpro.chat;

import pl.sages.javadevpro.view.ChatMessageProducer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatUser {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;
    private ChatRoom room;
    private UserCommandsHandler commandsController;
    private final ChatMessageProducer chatMessageProducer = new ChatMessageProducer();

    public ChatUser(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = reader.readLine();
        } catch (IOException e) {
            closeUser();
        }
    }

    public void listenToClient() {
        commandsController = new UserCommandsHandler(this);
        String inputFromClient;
        while (!socket.isClosed()) {
            try {
                inputFromClient = reader.readLine();
                handleClientInput(inputFromClient);
            } catch (IOException e) {
                closeUser();
                break;
            }
        }
    }

    public void writeToClient(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            closeUser();
        }
    }

    private void handleClientInput(String inputFromClient) {
        if (inputFromClient.startsWith("/")) {
            commandsController.dispatchCommand(inputFromClient);
        } else {
            String messageToUsers = chatMessageProducer.userMessage(inputFromClient,this);
            room.sendMessageToAllUsers(messageToUsers, this);
        }
    }

    public void closeUser() {
        try {
            if(writer != null) {
                writer.close();
            }
            if(reader != null) {
                reader.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public ChatRoom getRoom() {
        return room;
    }

    public void setRoom(ChatRoom room) {
        this.room = room;
    }


}