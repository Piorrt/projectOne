package pl.sages.javadevpro;

import jakarta.enterprise.event.Event;
import pl.sages.javadevpro.commons.TextReader;
import pl.sages.javadevpro.commons.TextWriter;

import java.net.Socket;

import static pl.sages.javadevpro.ServerEventType.CONNECTION_CLOSED;
import static pl.sages.javadevpro.ServerEventType.MESSAGE_RECEIVED;

class Worker implements Runnable {

    private final Socket socket;
    private final Event<ServerEvent> eventsHandler;
    private final TextWriter writer;

    private String roomName;


    Worker(Socket socket, Event<ServerEvent> eventsHandler, String roomName) {
        this.socket = socket;
        this.eventsHandler = eventsHandler;
        this.roomName = roomName;
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

    private void onText(String text) {
        eventsHandler.fire(new ServerEvent(MESSAGE_RECEIVED, text, this));
    }

    private void onInputClose() {
        eventsHandler.fire(new ServerEvent(CONNECTION_CLOSED, this));
    }

    void send(String text) {
        writer.write(text);
    }

}
