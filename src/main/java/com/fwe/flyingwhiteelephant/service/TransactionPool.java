package com.fwe.flyingwhiteelephant.service;

import com.fwe.flyingwhiteelephant.model.Transaction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class TransactionPool extends ArrayBlockingQueue<Transaction> {
    private final long threshold;
    private long startTime;
    private final AtomicBoolean startJobToMonitorQueue = new AtomicBoolean(false);
    private static final ScheduledExecutorService queueTimeoutExecutor = Executors.newScheduledThreadPool(10);
    private Consumer<TransactionPool> queueFullOrTimeoutListener;
    public TransactionPool(int capacity, long threshold) {
        super(capacity);
        this.threshold = threshold;
        this.startTime = System.currentTimeMillis();
    }

    public void addQueueEventListener(Consumer<TransactionPool> queueFullOrTimeoutListener) {
        this.queueFullOrTimeoutListener = queueFullOrTimeoutListener;
    }

    public boolean isTimeout(long threshold) {
        return System.currentTimeMillis() - startTime > threshold;   
    }

    public boolean isFull() {
        return this.remainingCapacity() == 0;
    }

    private Runnable getJobToMonitorQueue() {
        return () -> {
            if (isTimeout(threshold) || isFull()) {
                queueFullOrTimeoutListener.accept(this);
            }
        };
    }

    @Override
    public boolean add(@NonNull Transaction transaction) {
        if (isTimeout(threshold) || isFull()) {
            queueFullOrTimeoutListener.accept(this);
        }
        super.add(transaction);
        if (startJobToMonitorQueue.compareAndSet(false, true)) {
            queueTimeoutExecutor.scheduleAtFixedRate(
                    getJobToMonitorQueue(),
                    0, 100, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        return true;
    }

    public Transaction[] getTransactions() {
        return this.toArray(new Transaction[0]);
    }

    public void clear() {
        super.clear();
        this.startTime = System.currentTimeMillis();
    }
}
