package com.study.spring_study.task.manager;


/**
 * @ClassName TaskInfoValidator
 * @Description TaskInfoValidator
 * @Author Silwings
 * @Date 2022/6/14 15:22
 * @Version V1.0
 **/
public class TaskInfoValidator {
    
    private TaskInfoValidator() {
        // No codes
    }

    public static void validate(final TaskInfo taskInfo) {
        //CheckUtils.isNotNull(taskInfo, ErrorCodes.SCHEDULED_TASK_ERROR,"任务信息不可为空");
        //CheckUtils.isNotBlank(taskInfo.getTaskCode(), ErrorCodes.SCHEDULED_TASK_ERROR,"任务编码不可为空");
        //CheckUtils.isNotNull(taskInfo.getScheduledAnnotationMapping(), ErrorCodes.SCHEDULED_TASK_ERROR,"scheduledMapping不可为空");
    }

}
