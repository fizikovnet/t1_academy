package com.fizikovnet.hw3;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyThreadPool {

    private final Worker[] workers;
    private final LinkedList<Runnable> queue;
    private final AtomicBoolean isStopped;

    public MyThreadPool(int size) {
        this.workers = new Worker[size];
        this.queue = new LinkedList<>();
        this.isStopped = new AtomicBoolean(false);
        for (int i = 0; i < size; i++) {
            workers[i] = new Worker("Worker_" + i);
            workers[i].start();
        }
    }


    public void execute(Runnable task) {
        if (isStopped.get()) {
            throw new IllegalStateException("Thread pool is already closed");
        }
        synchronized (queue) {
            queue.offer(task);
            queue.notify();
        }
    }

    public void shutdown() {
        isStopped.set(true);
        synchronized (queue) {
            queue.notifyAll();
        }
    }

    public void awaitTermination() {
        for (Worker worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class Worker extends Thread {

        public Worker(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println("Thread - " + Thread.currentThread().getName() + " is created");
            Runnable task;
            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        if (isStopped.get()) {
                            return;
                        }
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    task = queue.removeFirst();
                }
                task.run();
            }
        }
    }
}
