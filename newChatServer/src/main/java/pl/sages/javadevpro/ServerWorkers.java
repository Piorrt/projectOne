package pl.sages.javadevpro;

import jakarta.inject.Singleton;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
class ServerWorkers {

    private final Set<Worker> workers = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    void add(Worker worker) {
        lock.writeLock().lock();
        workers.add(worker);
        lock.writeLock().unlock();
    }

    void remove(Worker worker) {
        lock.writeLock().lock();
        workers.remove(worker);
        lock.writeLock().unlock();
    }

    void broadcast(String text) {
        lock.readLock().lock();
        workers.forEach(worker -> worker.send(text));
        lock.readLock().unlock();
    }

}
