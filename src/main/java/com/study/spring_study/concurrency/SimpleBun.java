package com.study.spring_study.concurrency;

/**
 * 简单包子生产消费
 */
public class SimpleBun {

    private static String a = "";


    public static void main(String[] args) throws InterruptedException {
        //生产包子，消费包子
        new Thread(()->{

            while(true){
                synchronized (a){
                    System.out.println("生产包子");
                    a.notifyAll();
                    try {
                        Thread.sleep(500);
                        a.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }).start();

        Thread.sleep(500);

        new Thread(()->{

            while(true){
                synchronized (a){
                    System.out.println("吃包子");
                    a.notifyAll();

                    try {
                        Thread.sleep(500);
                        a.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }).start();

    }
}
