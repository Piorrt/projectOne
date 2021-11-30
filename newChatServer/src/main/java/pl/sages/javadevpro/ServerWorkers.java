package pl.sages.javadevpro;

import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
class ServerWorkers {

    private final Map<String,ChatRoom> rooms = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    void addUserToRoom(String roomName, Worker worker) {
        lock.writeLock().lock();
        ChatRoom chatRoom = rooms.get(roomName);
        if (chatRoom==null) {
            chatRoom = new ChatRoom(roomName);
            chatRoom.add(worker);
            rooms.put(roomName,chatRoom);
        }
        else {
            chatRoom.add(worker);
        }
        lock.writeLock().unlock();
    }

    void remove(String roomName, Worker worker) {
        lock.writeLock().lock();
        ChatRoom chatRoom = rooms.get(roomName);
        if (chatRoom!=null) {
            chatRoom.remove(worker);
        }
        lock.writeLock().unlock();
    }

    void broadcast(String text) {
        lock.readLock().lock();
        //TODO
//        workers.forEach(worker -> worker.send(text));
        lock.readLock().unlock();
    }

}
