package com.study.spring_study.task.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @ClassName TaskInfo
 * @Description 用于刷新任务的任务描述
 * @Author Silwings
 * @Date 2022/6/14 15:22
 * @Version V1.0
 **/
@Getter
@Setter(AccessLevel.PRIVATE)
@Accessors(chain = true)
public class TaskInfo {

    private String taskCode;

    // 执行计划
    private ScheduledAnnotationMapping scheduledAnnotationMapping;

    private long timestamp;

    public TaskInfo() {
        this.timestamp = 0L;
    }

    public static TaskInfo from(final TaskInfo taskInfo) {
        final TaskInfo info = new TaskInfo();
        info.setTaskCode(taskInfo.getTaskCode())
                .setScheduledAnnotationMapping(ScheduledAnnotationMapping.from(taskInfo.getScheduledAnnotationMapping()))
                .setTimestamp(taskInfo.getTimestamp());

        return info;
    }



    public static TaskInfo of(final String taskCode, final ScheduledAnnotationMapping scheduledAnnotationMapping) {
        return new TaskInfo()
                .setTaskCode(taskCode)
                .setScheduledAnnotationMapping(scheduledAnnotationMapping);
    }

    /**
     * 判断当前信息实例是否比 taskInfo 新,且存在新内容
     *
     * @param taskInfo
     * @return boolean
     * @author Silwings
     * @date 2022/6/11 21:18
     * @since 2.10
     */
    public boolean newer(final TaskInfo taskInfo) {
        return this.getScheduledAnnotationMapping().different(taskInfo.getScheduledAnnotationMapping()) && this.getTimestamp() >= taskInfo.getTimestamp();
    }

    public void modifyTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

}
