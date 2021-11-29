package pl.sages.javadevpro;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;

@Singleton
@Log
class ServerEventsLogger {

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
