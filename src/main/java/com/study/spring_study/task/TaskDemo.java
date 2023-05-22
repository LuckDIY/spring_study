package com.study.spring_study.task;

import cn.hutool.core.date.DateUtil;
import com.study.spring_study.task.manager.ScheduledTaskInfo;
import com.study.spring_study.task.manager.ScheduledTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
public class TaskDemo implements CommandLineRunner {

    @Autowired
    private ScheduledTaskManager scheduledTaskManager;

    @Scheduled(cron = "*/5 * * * * ?")
    public void aa(){

        log.info("定时任务测试:{}",DateUtil.now());
        Map<String, ScheduledTaskInfo> inExecutionScheduledTaskInfoMap = scheduledTaskManager.getInExecutionScheduledTaskInfoMap();
        ScheduledTaskInfo scheduledTaskInfo = inExecutionScheduledTaskInfoMap.get("com.study.spring_study.task.TaskDemo#aa");

        if(scheduledTaskInfo!=null){
            scheduledTaskInfo.getScheduledTask().cancel(false);
        }
    }




    public ScheduledFuture<?> test(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.initialize();
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(() -> {
            log.info("测试:{}", DateUtil.now());
        }, new CronTrigger("*/5 * * * * ?"));

        return schedule;
    }

    @Override
    public void run(String... args) throws Exception {
        test().cancel(false);
    }



    public String mergeAlternately(String word1, String word2) {

        StringBuilder str = new StringBuilder();

        int i = 0,j=0;

        while(i<word1.length() && j<word2.length()){
            str.append(word1.charAt(i)).append(word2.charAt(j));
            i++;
            j++;
        }

        for (;i<word1.length(); i++) {
            str.append(word1.charAt(i));
        }

        for (;j<word2.length(); j++) {
            str.append(word2.charAt(j));
        }
        return str.toString();
    }
}
