package pl.sages.javadevpro.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class Utils {

    private static final int MIN_PORT_RANGE = 80;
    private static final int MAX_PORT_RANGE = 9000;

    public static int readPortFromPropertyOrReturnDefault(String propertyName, int defaultPort){
        String propertyValue = System.getProperty(propertyName);
        if(propertyValue != null) {
            int temp = Integer.parseInt(propertyValue);
            if (temp >= MIN_PORT_RANGE && temp <= MAX_PORT_RANGE) {
                return temp;
            }
        }
        return defaultPort;
    }

    public static Server createServer(Class<? extends Server> serverClass, int port) throws IOException {
        ServerSocket socket = new ServerSocket(port);
        return new ServerFactory().create(serverClass, socket);
    }

}
