package pl.sages.javadevpro.utils;

import pl.sages.javadevpro.chat.ChatServer;
import pl.sages.javadevpro.ftp.FTPServer;

import java.net.ServerSocket;

public final class ServerFactory {
    public <T extends Server> Server create(Class<T> clazz, ServerSocket socket) {
        Server toReturn = null;
        if (ChatServer.class.equals(clazz)) {
            toReturn = new ChatServer(socket);
        } else if (FTPServer.class.equals(clazz)) {
            toReturn = new FTPServer(socket);
        }

        return toReturn;
    }
}
