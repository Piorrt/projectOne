package pl.sages.javadevpro;

import jakarta.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
class ServerWorkers {

    private Set<Worker> workers = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    boolean add(Worker worker) {
        lock.writeLock().lock();
        String existingWorker = workers
                .stream()
                .map(user -> user.getName())
                .filter(name -> worker.getName().equals(name))
                .findAny()
                .orElse(null);

        boolean result = true;
        if(existingWorker == null) {
            workers.add(worker);
        } else {
            result = false;
        }
        lock.writeLock().unlock();
        return result;
    }

    void remove(Worker worker) {
        lock.writeLock().lock();
        workers.remove(worker);
        lock.writeLock().unlock();
    }

    void broadcast(String text, String chatRoom) {
        lock.readLock().lock();
        workers.stream()
            .filter(worker -> worker.getRoomName().equals(chatRoom))
            .forEach(worker -> worker.send(text));
        lock.readLock().unlock();
    }

    //join to another room

}
