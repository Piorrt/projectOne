package pl.sages.javadevpro.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class Utils {

    public static int readPortFromPropertyOrReturnDefault(String propertyName, int defaultPort, int minRange, int maxRange){
        String propertyValue = System.getProperty(propertyName);
        if(propertyValue != null) {
            int temp = Integer.parseInt(propertyValue);
            if (temp >= minRange && temp <= maxRange) {
                return temp;
            }
        }
        return defaultPort;
    }

    public static Server createAndRunServer(Class serverClass, int port) throws IOException {
        ServerSocket socket = new ServerSocket(port);
        Server theServer = new ServerFactory().create(serverClass, socket);
        Thread thread = new Thread(theServer);
        thread.start();
        return theServer;
    }

}
