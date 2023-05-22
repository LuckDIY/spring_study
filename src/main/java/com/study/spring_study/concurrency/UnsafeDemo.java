package com.study.spring_study.concurrency;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Slf4j
public class UnsafeDemo {

    private final static Unsafe unsafe;
    private int a = 1;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws NoSuchFieldException {
        long a1 = unsafe.objectFieldOffset(UnsafeDemo.class.getDeclaredField("a"));
        log.info("获取在UnsafeDemo类中a字段的偏移量:{}",a1);
        UnsafeDemo unsafeDemo = new UnsafeDemo();
        log.info("a的值:{}",unsafeDemo.a);
        boolean b = unsafe.compareAndSwapInt(unsafeDemo, a1, 1, 2);
        log.info("b的值:{}",b);
        log.info("a的值:{}",unsafeDemo.a);
        boolean c = unsafe.compareAndSwapInt(unsafeDemo, a1, 1, 3);
        log.info("c的值:{}",c);
        log.info("a的值:{}",unsafeDemo.a);

    }

}
