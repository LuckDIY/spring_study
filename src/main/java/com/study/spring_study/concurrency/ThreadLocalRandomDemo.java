package com.study.spring_study.concurrency;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class ThreadLocalRandomDemo {
    public static void main(String[] args) {

        //randomTest();


        //线程私有种子，避免并发修改自旋导致资源浪费
        int i =0;
        while(i++<30){
            new Thread(()->{
                System.out.println(ThreadLocalRandom.current().nextInt(10));
            }).start();
        }
    }

    private static void randomTest() {
        Random random = new Random();

        int i =0;
        while(i++<30){
            new Thread(()->{
                System.out.println(random.nextInt(10));
            }).start();
        }
    }



}
