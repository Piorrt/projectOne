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

    Worker(Socket socket, Event<ServerEvent> eventsHandler) {
        this.socket = socket;
        this.eventsHandler = eventsHandler;
        writer = new TextWriter(socket);
    }

    @Override
    public void run() {
        new TextReader(socket, this::onText, this::onInputClose).read();
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
