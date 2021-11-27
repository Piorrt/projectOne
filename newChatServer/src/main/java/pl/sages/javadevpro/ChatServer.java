package pl.sages.javadevpro;

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

@RequiredArgsConstructor
public class ChatServer {

    private static final String CHAT_PORT_PARAMETER_NAME = "chatServerPort";
    private static final int DEFAULT_CHAT_PORT = 8888;
    private static final int THREADS_COUNT = 1024;

    private final ServerWorkers serverWorkers = new ServerWorkers();
    private final EventsBus eventsBus;
    private final ExecutorService executorService;

    private void start(int port) throws IOException {
        eventsBus.addConsumer(new ServerEventsProcessor(serverWorkers));
        var serverSocket = new ServerSocket(port);
        eventsBus.publish(new ServerEvent(STARTED));
        while (true) {
            var socket = serverSocket.accept();
            eventsBus.publish(new ServerEvent(CONNECTION_ACCEPTED));
            createWorker(socket);
        }
    }

    private void createWorker(Socket socket) {
        var worker = new Worker(socket, eventsBus);
        serverWorkers.add(worker);
        executorService.execute(worker);
    }

    public static void main(String[] args) throws IOException {
        var port = Sockets.parsePort(getProperty(CHAT_PORT_PARAMETER_NAME), DEFAULT_CHAT_PORT);
        var eventsBus = new EventsBus();
        eventsBus.addConsumer(new ServerEventsLogger());
        var server = new ChatServer(eventsBus, newFixedThreadPool(THREADS_COUNT));
        server.start(port);
    }

}

