package pl.sages.javadevpro;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedSet;

@ApplicationScoped
class EventsBus {

    private final Set<Consumer<ServerEvent>> consumers = synchronizedSet(new HashSet<>());

    void addConsumer(Consumer<ServerEvent> consumer) {
        consumers.add(consumer);
    }

    void publish(ServerEvent event) {
        consumers.forEach(consumer -> consumer.accept(event));
    }

}
