package pl.sages.javadevpro;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ServerEventsProcessor implements Consumer<ServerEvent> {

    private final ServerWorkers serverWorkers;

    @Override
    public void accept(@Observes ServerEvent event) {
        switch (event.getType()) {
            case MESSAGE_RECEIVED:
                serverWorkers.broadcast(event.getPayload());
                break;
            case CONNECTION_CLOSED:
                serverWorkers.remove(event.getSource());
                break;
        }
    }

}
