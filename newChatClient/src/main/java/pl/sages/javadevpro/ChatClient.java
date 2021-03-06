package pl.sages.javadevpro;

import lombok.extern.java.Log;
import pl.sages.javadevpro.commons.TextReader;
import pl.sages.javadevpro.commons.TextWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import static java.lang.System.exit;

@Log
public class ChatClient {

    private final Consumer<String> onMessage;
    private final Runnable readFromSocket;
    private final Runnable readFromConsole;
    private final Socket socket;
    private final String name;
    private static String[] names = {"Ola", "Ala", "Ula", "Ela", "Jan"};

    public ChatClient(String host, int port, String name) throws IOException {
        socket = new Socket(host, port);
        this.name = name;

        onMessage = text -> new TextWriter(socket).write(name + ": " + text);
        readFromSocket = () -> new TextReader(socket, System.out::println, () -> System.out.println("Connection closed")).read();
        readFromConsole = () -> new TextReader(System.in, onMessage).read();
    }

    private void start() {
        onMessage.accept(name);
        new Thread(readFromSocket).start();
        var consoleReader = new Thread(readFromConsole);
        consoleReader.setDaemon(true);
        consoleReader.start();
    }

    public static void main(String[] args) throws IOException {
        Integer randomInt = new Random().nextInt(names.length);
        new ChatClient("localhost", 8888, names[randomInt]).start();
    }

}
