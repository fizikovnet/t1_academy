package com.fizikovnet.hw3;

import java.time.Duration;

public class Main {

    public static void main(String[] args) {
        MyThreadPool pool = new MyThreadPool(3);

        for (int i = 0; i < 30; i++) {
            final int fi = i;
            pool.execute(() -> {
                System.out.println("Job #" + fi + " is started in thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(Duration.ofSeconds(1));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Job #" + fi + " is completed");
            });
        }

        pool.shutdown();
        pool.awaitTermination();
    }

}
