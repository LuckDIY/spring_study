package com.study.spring_study.task.manager;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * @ClassName SchedulingConfig
 * @Description 定时任务配置
 * @Author Silwings
 * @Date 2022/6/14 15:18
 * @Version V1.0
 **/
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    private ScheduledTaskRegistrar taskRegistrar;

    /**
     * ScheduledAnnotationBeanPostProcessor.finishRegistration会调用
     * @param taskRegistrar the registrar to be configured.
     */
    @Override
    public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {

        //获取 ScheduledTaskRegistrar ,
        this.taskRegistrar = taskRegistrar;

        // 已配置过后不重复配置,这里只要能拿到 ScheduledTaskRegistrar 即可
        final TaskScheduler scheduler = taskRegistrar.getScheduler();
        if (null != scheduler) {
            return;
        }

        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.setThreadNamePrefix("schedule-task-");
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(8, threadPoolTaskScheduler));
    }

    public ScheduledTaskRegistrar getTaskRegistrar() {
        return this.taskRegistrar;
    }
}

