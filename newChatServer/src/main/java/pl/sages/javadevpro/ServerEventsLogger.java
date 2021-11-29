package pl.sages.javadevpro;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;

import java.util.function.Consumer;

@Singleton
@Log
class ServerEventsLogger implements Consumer<ServerEvent> {

    @Override
    public void accept(@Observes ServerEvent event) {
        switch (event.getType()) {
            case STARTED:
                log.info("Server started.");
                break;
            case CONNECTION_ACCEPTED:
                log.info("New connection accepted.");
                break;
            case CONNECTION_CLOSED:
                log.info("Connection form client closed.");
                break;
        }
    }

}
