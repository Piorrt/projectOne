package pl.sages.javadevpro;

import lombok.Getter;

@Getter
class ServerEvent {

    private final ServerEventType type;
    private String payload;
    private Worker source;

    ServerEvent(ServerEventType type) {
        this.type = type;
    }

    ServerEvent(ServerEventType type,  Worker source) {
        this.type = type;
        this.source = source;
    }

    ServerEvent(ServerEventType type, String payload, Worker source) {
        this.type = type;
        this.payload = payload;
        this.source = source;
    }

}
