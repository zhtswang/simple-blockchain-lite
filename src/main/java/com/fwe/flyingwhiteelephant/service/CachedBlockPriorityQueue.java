package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.model.Block;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class CachedBlockPriorityQueue {
    private final PriorityBlockingQueue<Block> cachedBlockPriorityQueue;
    private final CopyOnWriteArrayList<Consumer<Block>> listeners;
    private final ExecutorService consumerExecutorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean consumerIsRunning = new AtomicBoolean(false);
    public CachedBlockPriorityQueue() {
        this.cachedBlockPriorityQueue =  new PriorityBlockingQueue<>(100,
                (o1, o2) -> (int) (o1.getHeader().getHeight() - o2.getHeader().getHeight()));
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public void addListener(Consumer<Block> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<Block> listener) {
        listeners.remove(listener);
    }

    public void put(Block item) {
        cachedBlockPriorityQueue.put(item); // Add the item to the queue
        notifyListeners(); // Notify all listeners
    }

    private void notifyListeners() {
        for (Consumer<Block> listener : listeners) {
            consumerExecutorService.submit(() -> {
                while(!cachedBlockPriorityQueue.isEmpty() && consumerIsRunning.compareAndSet(false, true)) {
                    Block item = peek();
                    listener.accept(item);
                    consumerIsRunning.set(false);
                }
            });
        }
    }

    public void poll() {
        cachedBlockPriorityQueue.poll();
    }

    public Block peek() {
        return cachedBlockPriorityQueue.peek(); // Return the head of the queue without removing it
    }

    public boolean isEmpty() {
        return cachedBlockPriorityQueue.isEmpty(); // Check if the queue is empty
    }
}
