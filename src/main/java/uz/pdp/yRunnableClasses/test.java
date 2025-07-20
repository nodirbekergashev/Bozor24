package uz.pdp.yRunnableClasses;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.Queue;

public class test {
    public static void main(String[] args) {
        SharedClass sharedClass = new SharedClass();

        Thread t1 = new Producer(sharedClass);
        Thread t2 = new Consumer(sharedClass);

        t1.start();
        t2.start();
    }


    @RequiredArgsConstructor
    private static class Producer extends Thread {
        private final SharedClass sharedClass;

        @SneakyThrows
        @Override
        public void run() {
            while (true) {
                sharedClass.produce();
            }
        }
    }

    @RequiredArgsConstructor
    private static class Consumer extends Thread {
        private final SharedClass sharedClass;

        @SneakyThrows
        @Override
        public void run() {
            while (true) {
                sharedClass.consumer();
            }
        }
    }

    private static class SharedClass {
        private final Queue<Integer> queue = new LinkedList<>();

        public void produce() throws InterruptedException {
            synchronized (queue) {
                while (queue.size() == 100) {
                    queue.wait();
                }
                queue.add(100);
                System.out.println(" ===== Produce ==== size: " + queue.size());
                queue.notifyAll();
            }
            Thread.sleep(1);
        }

        public void consumer() throws InterruptedException {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    queue.wait();
                }
                System.out.println(" ===== Consume ==== size: " + queue.size());
                queue.poll();
                queue.notifyAll();
            }
        }
    }
}

