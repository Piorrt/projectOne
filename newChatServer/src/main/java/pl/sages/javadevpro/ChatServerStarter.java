package pl.sages.javadevpro;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.io.IOException;

public class ChatServerStarter {

    public static void main(String[] args) throws IOException {
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();
        container.select(ChatServer.class).get().start();
        container.shutdown();
    }


}
