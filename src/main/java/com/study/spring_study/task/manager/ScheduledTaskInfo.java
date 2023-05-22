package com.study.spring_study.task.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.scheduling.config.ScheduledTask;

/**
 * @ClassName ScheduledTaskInfo
 * @Description spring容器中的任务信息
 * @Author Silwings
 * @Date 2022/6/14 15:22
 * @Version V1.0
 **/
@Getter
@Setter(AccessLevel.PRIVATE)
@Accessors(chain = true)
public class ScheduledTaskInfo {

    private TaskInfo taskInfo;

    private ScheduledTask scheduledTask;

    public static ScheduledTaskInfo of(final TaskInfo taskInfo, final ScheduledTask scheduledTask) {
        return new ScheduledTaskInfo()
                .setTaskInfo(taskInfo)
                .setScheduledTask(scheduledTask);
    }

    public static ScheduledTaskInfo from(final ScheduledTaskInfo scheduledTaskInfo) {
        if (null == scheduledTaskInfo) {
            return null;
        }
        return ScheduledTaskInfo.of(scheduledTaskInfo.getTaskInfo(), scheduledTaskInfo.getScheduledTask());
    }
}
