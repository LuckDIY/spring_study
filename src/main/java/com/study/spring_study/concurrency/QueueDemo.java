package com.study.spring_study.concurrency;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueDemo {
    public static void main(String[] args) {

        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.offer("666");

        System.out.println(queue.poll());
    }
}
