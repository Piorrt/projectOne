package pl.sages.javadevpro;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;


@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ServerCommandsProcessor {

    private final ServerWorkers serverWorkers;

    public void accept(@Observes ServerEvent event) {
        switch (event.getType()) {
            case COMMAND_RECEIVED:
                break;
        }
    }

}
