package pl.sages.javadevpro;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import pl.sages.javadevpro.commons.Sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.lang.System.exit;
import static java.lang.System.getProperty;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static pl.sages.javadevpro.ServerEventType.CONNECTION_ACCEPTED;
import static pl.sages.javadevpro.ServerEventType.STARTED;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ChatServer {

    private static final String CHAT_PORT_PARAMETER_NAME = "chatServerPort";
    private static final int DEFAULT_CHAT_PORT = 8888;
    private static final int THREADS_COUNT = 1024;

    private final ServerWorkers serverWorkers;
    @Inject
    private Event<ServerEvent> eventsHandler;

    private ExecutorService executorService;
    private int port;

    @PostConstruct
    private void beforeStart(){
        executorService = newFixedThreadPool(THREADS_COUNT);
        port = Sockets.parsePort(getProperty(CHAT_PORT_PARAMETER_NAME), DEFAULT_CHAT_PORT);
    }

    public void start() throws IOException {
        var serverSocket = new ServerSocket(port);
        eventsHandler.fire(new ServerEvent(STARTED));
        while (true) {
            var socket = serverSocket.accept();
            eventsHandler.fire(new ServerEvent(CONNECTION_ACCEPTED));

            String name = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
            createWorker(socket, name);
        }
    }

    private void createWorker(Socket socket, String name) throws IOException {
        var worker = new Worker(socket, eventsHandler, name, "general");
        if (serverWorkers.add(worker)){
            new PrintWriter(socket.getOutputStream(), true).println("SERVER: Name accepted.");
        } else {
            new PrintWriter(socket.getOutputStream(), true).println("SERVER: Name already exist!");
        }
        executorService.execute(worker);
    }

}

