package pl.sages.javadevpro;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import pl.sages.javadevpro.commons.Sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

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
    private final EventsBus eventsBus;
    @Inject
    private Event<ServerEvent> serverEvent;

    private ExecutorService executorService;
    private int port;

    @PostConstruct
    private void beforeStart(){
        executorService = newFixedThreadPool(THREADS_COUNT);
        port = Sockets.parsePort(getProperty(CHAT_PORT_PARAMETER_NAME), DEFAULT_CHAT_PORT);
    }

    public void start() throws IOException {
        var serverSocket = new ServerSocket(port);
        serverEvent.fire(new ServerEvent(STARTED));
        while (true) {
            var socket = serverSocket.accept();
            serverEvent.fire(new ServerEvent(CONNECTION_ACCEPTED));
            createWorker(socket);
        }
    }

    private void createWorker(Socket socket) {
        var worker = new Worker(socket, eventsBus);
        serverWorkers.add(worker);
        executorService.execute(worker);
    }

}

