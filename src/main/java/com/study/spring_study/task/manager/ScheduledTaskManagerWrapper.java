package com.study.spring_study.task.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @ClassName ScheduledTaskManager
 * @Description Spring 定时任务管理
 * @Author Silwings
 * @Date 2022/6/14 15:22
 * @Version V1.0
 **/
@Slf4j
@Component
public class ScheduledTaskManagerWrapper {

    @Autowired
    private ScheduledTaskManager scheduledTaskManager;

    /*public void refresh(final ScheduledRefreshInfo scheduledRefreshInfo) {

        if (null == scheduledRefreshInfo) {
            return;
        }

        // 前置检查: 指定的taskCode应该在spring容器中存在(ScheduledTaskManager.refresh中如果出现找不到的定时任务会抛出异常,这里提前检查是为了提高效率)
        final ScheduledTask scheduledTask;
        try {
            final ScheduledTaskInfo scheduledAnnotationTask = this.scheduledTaskManager.getScheduledAnnotationTask(scheduledRefreshInfo.getTaskCode());
            scheduledTask = scheduledAnnotationTask.getScheduledTask();
        } catch (Exception e) {
            log.error("检查到 taskCode为{}的定时任务在Spring容器中不存在,忽略此次更新.", scheduledRefreshInfo.getTaskCode());
            return;
        }

        // 在调用web刷新接口后,如果数据库配置信息被修改了但没有确认,这里不应该错误的将数据库配置信息用来作为刷新数据源
        // 从 scheduledLastUpdateInfo 中获取配置信息以确保该映射信息和其lastUpdateTime对应的请求时间时的配置信息一致.
        final ScheduledMappingDto scheduledMapping = scheduledRefreshInfo.getScheduledMapping();

        final TaskInfo taskInfo;

        // 不存在配置信息时表示需要按照方法注解重新注册定时任务
        if (null == scheduledMapping) {
            taskInfo = this.scheduledTaskManager.buildTaskInfoFromScheduledMethodAnnotation(scheduledTask);
            // 必须将时间戳更新为此时redis中的时间戳信息,否则 ScheduledTaskManager 将不认为这是一个最新的配置信息
            // ? 这里有一个待商酌的问题,是更新为scheduledLastUpdateInfo中的updateTime合适还是更新为当前执行时间合适
            taskInfo.modifyTimestamp(scheduledRefreshInfo.getLastUpdateTime());
        } else {
            taskInfo = TaskInfo.from(scheduledMapping);
        }

        try {
            // 刷新方法可能抛出异常,打印错误信息即可
            this.scheduledTaskManager.refresh(taskInfo);
        } catch (Exception e) {
            log.error("刷新任务执行失败,任务编码: " + scheduledRefreshInfo.getTaskCode(), e);
        }
    }

    *//**
     * 关闭指定任务
     *
     * @param taskCode              任务编码
     * @param mayInterruptIfRunning 如果仍在运行，是否强制中断任务（指定 false 以允许任务完成）
     * @author Silwings
     * @date 2022/6/12 20:41
     * @since 2.10
     *//*
    public void cancel(final String taskCode, final boolean mayInterruptIfRunning) {
        this.scheduledTaskManager.cancelTask(taskCode, mayInterruptIfRunning);
    }

    *//**
     * 手动执行一次定时任务,该任务不限制是否是生效中的定时任务
     *
     * @param taskCode 任务编码
     * @author Silwings
     * @date 2022/6/12 21:07
     * @see ScheduledAnnotationBeanPostProcessor#createRunnable(Object, Method)
     * @since 2.10
     *//*
    public void execute(final String taskCode) {
        // ScheduledTaskInfo中的ScheduledTask中保存了Runnable实例,该实例中包含的实际执行代码是从spring容器中获取的经过aop代理的方法
        this.scheduledTaskManager.getScheduledAnnotationTask(taskCode)
                .getScheduledTask().getTask().getRunnable().run();
    }

    public Set<String> getInExecutionTaskCodes() {
        return this.scheduledTaskManager.getInExecutionTaskCodes(null);
    }

    *//**
     * 获取所有生效中定时任务的任务编码
     *
     * @param exceptTaskCodes 需要排除的任务编码
     * @return java.util.Set<java.lang.String> 任务编码集
     * @author Silwings
     * @date 2022/6/13 9:16
     * @since 2.10
     *//*
    public Set<String> getInExecutionTaskCodes(final String[] exceptTaskCodes) {
        return this.scheduledTaskManager.getInExecutionTaskCodes(exceptTaskCodes);
    }

    public Set<String> getScheduledAnnotationTaskCodes() {
        return this.scheduledTaskManager.getScheduledAnnotationTaskCodes(null);
    }

    public void cancel(final ScheduledCancelInfo scheduledRefreshInfo) {

        if (null == scheduledRefreshInfo) {
            return;
        }

        this.cancel(scheduledRefreshInfo.getTaskCode(), scheduledRefreshInfo.isMandatory());
    }


    public void refresh(ScheduledMappingDto scheduledMappingDto){

        // 刷新方法可能抛出异常,打印错误信息即可
        this.scheduledTaskManager.refresh(TaskInfo.from(scheduledMappingDto));
    }*/
}
