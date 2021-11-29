package pl.sages.javadevpro.commons;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class Sockets {

    private static final int MIN_PORT_NUMBER = 80;
    private static final int MAX_PORT_NUMBER = 9000;

    public static int parsePort(String text, int defaultPort) {
        if (text == null) {
            return defaultPort;
        }
        var port = Integer.parseInt(text);
        return isAllowedPort(port) ? port : defaultPort;
    }

    private static boolean isAllowedPort(int portNumber) {
        return portNumber >= MIN_PORT_NUMBER && portNumber <= MAX_PORT_NUMBER;
    }

}
