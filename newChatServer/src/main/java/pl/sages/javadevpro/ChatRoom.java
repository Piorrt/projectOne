package pl.sages.javadevpro;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatRoom {

    private Set<Worker> roomWorkers = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private String roomName;

    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }

    void add(Worker worker) {
        lock.writeLock().lock();
        roomWorkers.add(worker);
        lock.writeLock().unlock();
    }

    void remove(Worker worker) {
        lock.writeLock().lock();
        roomWorkers.remove(worker);
        lock.writeLock().unlock();
    }

    void broadcast(String text) {
        lock.readLock().lock();
        roomWorkers.forEach(worker -> worker.send(text));
        lock.readLock().unlock();
    }


}
