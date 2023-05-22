package com.study.spring_study.concurrency;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class AtomicOperate{
        //通过cas保证线程安全，缺点，竞争激烈时容易自旋导致浪费cpu资源
        static AtomicLong atomicLong = new AtomicLong();
        
        //避免 竞争激烈时容易自旋导致浪费cpu资源，通过对共享资源分拆多个来解决，最终结果 cell[] + base = 结果
        static LongAdder longAdder = new LongAdder();
        
        
        //提供函数式原子操作，Long::sum ==> LongAdder,它可以加减乘除等所有操作，通过LongBinaryOperator传入操作
        static LongAccumulator longAccumulator = new LongAccumulator(Long::sum,0);


        public static void main(String[] args) {
            longAccumulator.accumulate(1L);
        }
        
        
        
        
        
        
    }