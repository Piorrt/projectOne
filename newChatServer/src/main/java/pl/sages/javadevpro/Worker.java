package pl.sages.javadevpro;

import jakarta.enterprise.event.Event;
import pl.sages.javadevpro.commons.TextReader;
import pl.sages.javadevpro.commons.TextWriter;

import java.net.Socket;

import static pl.sages.javadevpro.ServerEventType.*;

class Worker implements Runnable {

    private final Socket socket;
    private final Event<ServerEvent> eventsHandler;
    private final TextWriter writer;

    private String roomName;
    private String name;


    Worker(Socket socket, Event<ServerEvent> eventsHandler, String name, String roomName) {
        this.socket = socket;
        this.eventsHandler = eventsHandler;
        this.roomName = roomName;
        this.name = name;
        writer = new TextWriter(socket);
    }

    @Override
    public void run() {
        new TextReader(socket, this::onText, this::onInputClose).read();
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getName() {
        return name;
    }

    private void onText(String text) {
        if(text.startsWith("/")) {
            eventsHandler.fire(new ServerEvent(COMMAND_RECEIVED, text, this));
        } else {
            eventsHandler.fire(new ServerEvent(MESSAGE_RECEIVED, text, this));
        }
    }

    private void onInputClose() {
        eventsHandler.fire(new ServerEvent(CONNECTION_CLOSED, this));
    }

    void send(String text) {
        writer.write(text);
    }

}
