package pl.sages.javadevpro;

import lombok.extern.java.Log;
import pl.sages.javadevpro.commons.TextReader;
import pl.sages.javadevpro.commons.TextWriter;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.function.Consumer;

@Log
public class ChatClient {

    private final Consumer<String> onMessage;
    private final Runnable readFromSocket;
    private final Runnable readFromConsole;

    public ChatClient(String host, int port, String name) throws IOException {
        var socket = new Socket(host, port);
        onMessage = text -> new TextWriter(socket).write(name + ": " + text);
        readFromSocket = () -> new TextReader(socket, log::info, () -> log.info("Connection closed")).read();
        readFromConsole = () -> new TextReader(System.in, onMessage).read();
    }

    private void start() {
        new Thread(readFromSocket).start();
        var consoleReader = new Thread(readFromConsole);
        consoleReader.setDaemon(true);
        consoleReader.start();
    }

    public static void main(String[] args) throws IOException {
        new ChatClient("localhost", 8888, UUID.randomUUID().toString()).start();
    }

}
