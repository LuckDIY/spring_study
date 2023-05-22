package com.study.spring_study.task.manager;

import cn.hutool.core.date.Month;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName TaskManager
 * @Description 该方法的核心类中使用了和ScheduledAnnotationBeanPostProcessor相同的处理方式, 不要替换{@link StringUtils}的实现
 * @Author Silwings
 * @Date 2022/6/14 15:22
 * @Version V1.0
 **/
@Slf4j
@Component
public class ScheduledTaskManager implements CommandLineRunner, EmbeddedValueResolverAware, DisposableBean {

    @Autowired
    private SchedulingConfig schedulingConfig;
    @Autowired
    private ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;

    //@Autowired
    //private ScheduledMappingService scheduledMappingService;

    @Nullable
    private StringValueResolver embeddedValueResolver;
    // 注册到spring容器中的所有的定时任务信息
    private final Map<String, ScheduledTaskInfo> scheduledAnnotationTaskMap = new ConcurrentHashMap<>();
    // 生效中的定时任务信息(指在定时任务队列,不是指@Scheduled标记的方法正在执行)
    private final Map<String, ScheduledTaskInfo> inExecutionScheduledTaskInfoMap = new ConcurrentHashMap<>();
    // 初始化标志位,用于保证CommandLineRunner#run被重复调用也不会产生错误的结果
    private final AtomicInteger initializationFlag = new AtomicInteger(0);


    public static final String TASK_CODE_JOINER = "#";

    /**
     * 初始化定时任务信息
     * 1.收集注册到spring容器的所有定时任务信息
     * 2.对数据库配置和spring容器配置信息不同的定时任务进行刷新
     *
     * @author Silwings
     * @date 2022/6/11 18:03
     * @since 2.10
     */
    @Override
    public void run(final String... args) {

        // 防止run方法被重复调用
        if (this.initializationFlag.incrementAndGet()!=1) {
            log.error("TaskManager.run 不应该被反复调用!");
            return;
        }

        // 通过spring的ScheduledAnnotationBeanPostProcessor获取已注册的定时任务集
        final Set<ScheduledTask> scheduledTasks = this.scheduledAnnotationBeanPostProcessor.getScheduledTasks();

        //spring定时任务中的定时任务列表封装
        final List<ScheduledTaskInfo> scheduledTaskInfoList = new ArrayList<>();

        for (ScheduledTask scheduledTask : scheduledTasks) {

            final TaskInfo taskInfo = this.buildTaskInfoFromScheduledMethodAnnotation(scheduledTask);

            scheduledTaskInfoList.add(ScheduledTaskInfo.of(taskInfo, scheduledTask));
        }



        //初始化scheduledAnnotationTaskMap，spring所有定时任务都在这里
        this.initScheduledAnnotationTask(scheduledTaskInfoList);
        // 初始化inExecutionScheduledTaskInfoMap,所有spring容器中的定时任务均是生效中的定时任务
        this.initInExecutionScheduledTask(scheduledTaskInfoList);
        // 对数据库配置过的定时任务进行刷新
        this.registerDatabaseTask(scheduledTaskInfoList);
    }

    /**
     * 使用定时任务方法上的注解构建TaskInfo
     *
     * @param scheduledTask 调度任务
     * @return top.silwings.core.TaskInfo 任务信息
     * @author Silwings
     * @date 2022/6/12 17:26
     * @since 2.10
     */
    protected TaskInfo buildTaskInfoFromScheduledMethodAnnotation(final ScheduledTask scheduledTask) {

        final ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) scheduledTask.getTask().getRunnable();
        final Method method = runnable.getMethod();

        final Scheduled scheduledAnnotation = method.getAnnotation(Scheduled.class);

        // taskCode为全限定类名+TaskConstant.TASK_CODE_JOINER+方法名
        final String taskCode = this.buildTaskCode(method);

        return TaskInfo.of(taskCode, ScheduledAnnotationMapping.from(scheduledAnnotation));
    }

    private String buildTaskCode(final Method method) {
        return method.getDeclaringClass().getName() + TASK_CODE_JOINER + method.getName();
    }

    private void registerDatabaseTask(final List<ScheduledTaskInfo> scheduledTaskInfoList) {
        if (CollectionUtils.isEmpty(scheduledTaskInfoList)) {
            return;
        }

        //数据库配置的所有定时任务
        final List<TaskInfo> dbTaskInfoList = null;

        if (CollectionUtils.isEmpty(dbTaskInfoList)) {
            return;
        }

        final Map<String, ScheduledTaskInfo> taskCodeScheduledTaskInfoMap = scheduledTaskInfoList.stream()
                .collect(Collectors.toMap(item -> item.getTaskInfo().getTaskCode(), Function.identity(), (v1, v2) -> v2));

        final List<TaskInfo> waitReRegister = new ArrayList<>();

        for (TaskInfo taskInfo : dbTaskInfoList) {
            final ScheduledTaskInfo scheduledTaskInfo = taskCodeScheduledTaskInfoMap.get(taskInfo.getTaskCode());
            if (null == scheduledTaskInfo) {
                continue;
            }
            // 数据库的数据较新时才需要重新注册
            if (taskInfo.newer(scheduledTaskInfo.getTaskInfo())) {
                waitReRegister.add(taskInfo);
            }
        }

        waitReRegister.forEach(this::refresh);
    }

    /**
     * 取消指定的定时任务,并根据给定配置信息重新注册定时任务
     * 如果给定的配置信息不满足要求不会取消已有的定时任务
     * 该方法的注册流程参考 ScheduledAnnotationBeanPostProcessor
     *
     * @param infoArg 定时任务配置信息
     * @author Silwings
     * @date 2022/6/12 21:23
     * @see ScheduledAnnotationBeanPostProcessor#processScheduled(Scheduled, Method, Object)
     * @since 2.10
     */
    protected void refresh(final TaskInfo infoArg) {

        // 对TaskInfo进行基本的检查
        TaskInfoValidator.validate(infoArg);

        // 检查该任务信息是否满足刷新条件
        if (!this.needRefresh(infoArg)) {
            //log.debug("不需要更新,任务信息: {}", JSON.toJSONString(infoArg));
            return;
        }

        // copy出全新的info类,防止外界修改
        final TaskInfo taskInfo = TaskInfo.from(infoArg);

        try {

            final Runnable runnable = this.findRunnable(infoArg.getTaskCode());

            // 参考 ScheduledAnnotationBeanPostProcessor 设置的安全标识
            boolean processedSchedule = false;
            final String errorMessage = "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";
            final String stringToLongErrMsg = "\" - cannot parse into long";

            final ScheduledAnnotationMapping scheduled = taskInfo.getScheduledAnnotationMapping();

            CronTask cronTask = null;
            FixedDelayTask fixedDelayTask = null;
            FixedRateTask fixedRateTask = null;

            // 确定初始延迟
            long initialDelay = this.confirmInitialDelay(scheduled, stringToLongErrMsg);

            // Check cron expression
            String cron = scheduled.getCron();
            if (StringUtils.hasText(cron)) {
                String zone = scheduled.getZone();
                if (this.embeddedValueResolver != null) {
                    cron = this.embeddedValueResolver.resolveStringValue(cron);
                    zone = this.embeddedValueResolver.resolveStringValue(zone);
                }
                if (StringUtils.hasLength(cron)) {
                    Assert.isTrue(initialDelay == -1,"'initialDelay' not supported for cron triggers");
                    processedSchedule = true;
                    if (!Scheduled.CRON_DISABLED.equals(cron)) {
                        TimeZone timeZone;
                        if (StringUtils.hasText(zone)) {
                            timeZone = StringUtils.parseTimeZoneString(zone);
                        } else {
                            timeZone = TimeZone.getDefault();
                        }
                        cronTask = new CronTask(runnable, new CronTrigger(cron, timeZone));
                    }
                }
            }

            // At this point we don't need to differentiate between initial delay set or not anymore
            if (initialDelay < 0) {
                initialDelay = 0;
            }

            // Check fixed delay
            long fixedDelay = convertToMillis(scheduled.getFixedDelay(), scheduled.getTimeUnit());
            if (fixedDelay >= 0) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                fixedDelayTask = new FixedDelayTask(runnable, fixedDelay, initialDelay + this.getAlignTime());
            }

            String fixedDelayString = scheduled.getFixedDelayString();
            if (StringUtils.hasText(fixedDelayString)) {
                if (this.embeddedValueResolver != null) {
                    fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
                }
                if (StringUtils.hasLength(fixedDelayString)) {
                    Assert.isTrue(!processedSchedule,  errorMessage);
                    processedSchedule = true;
                    try {
                        fixedDelay = convertToMillis(fixedDelayString, scheduled.getTimeUnit());
                    } catch (RuntimeException ex) {
                        throw new IllegalArgumentException("Invalid fixedDelayString value \"" + fixedDelayString + stringToLongErrMsg);
                    }
                    fixedDelayTask = new FixedDelayTask(runnable, fixedDelay, initialDelay + this.getAlignTime());
                }
            }

            // Check fixed rate
            long fixedRate = convertToMillis(scheduled.getFixedRate(), scheduled.getTimeUnit());
            if (fixedRate >= 0) {
                Assert.isTrue(!processedSchedule,  errorMessage);
                processedSchedule = true;
                fixedRateTask = new FixedRateTask(runnable, fixedRate, initialDelay + this.getAlignTime());
            }
            String fixedRateString = scheduled.getFixedRateString();
            if (StringUtils.hasText(fixedRateString)) {
                if (this.embeddedValueResolver != null) {
                    fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
                }
                if (StringUtils.hasLength(fixedRateString)) {
                    Assert.isTrue(!processedSchedule,  errorMessage);
                    processedSchedule = true;
                    try {
                        fixedRate = convertToMillis(fixedRateString, scheduled.getTimeUnit());
                    } catch (RuntimeException ex) {
                        throw new IllegalArgumentException(
                                "Invalid fixedRateString value \"" + fixedRateString + stringToLongErrMsg);
                    }
                    fixedRateTask = new FixedRateTask(runnable, fixedRate, initialDelay + this.getAlignTime());
                }
            }

            // Check whether we had any attribute set
            Assert.isTrue(processedSchedule, errorMessage);

            // 关闭现有的定时任务
            this.cancelTask(taskInfo.getTaskCode(), false);

            // 开始注册定时任务
            ScheduledTask scheduledTask = null;
            final ScheduledTaskRegistrar registrar = this.schedulingConfig.getTaskRegistrar();
            if (null != cronTask) {
                scheduledTask = registrar.scheduleCronTask(cronTask);
                log.debug("注册了一个 cronTask");
            } else if (null != fixedDelayTask) {
                scheduledTask = registrar.scheduleFixedDelayTask(fixedDelayTask);
                log.debug("注册了一个 fixedDelayTask");
            } else if (null != fixedRateTask) {
                scheduledTask = registrar.scheduleFixedRateTask(fixedRateTask);
                log.debug("注册了一个 fixedRateTask");
            }

            // 记录刷新后的任务信息
            final ScheduledTaskInfo scheduledTaskInfo = ScheduledTaskInfo.of(taskInfo, scheduledTask);
            this.addInExecutionTask(scheduledTaskInfo);
            this.addScheduledAnnotationTask(scheduledTaskInfo);

            log.info("定时任务 {} 刷新完成. 新的执行计划: {}", taskInfo.getTaskCode(), taskInfo.getScheduledAnnotationMapping());

        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("刷新定时任务失败. " + JSON.toJSONString(taskInfo), ex);
        }
    }
    
    private long confirmInitialDelay(final ScheduledAnnotationMapping scheduled, final String stringToLongErrMsg) {

        long initialDelay = convertToMillis(scheduled.getInitialDelay(), scheduled.getTimeUnit());
        String initialDelayString = scheduled.getInitialDelayString();
        if (StringUtils.hasText(initialDelayString)) {
            Assert.isTrue(initialDelay < 0,  "Specify 'initialDelay' or 'initialDelayString', not both");
            if (this.embeddedValueResolver != null) {
                initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
            }
            if (StringUtils.hasLength(initialDelayString)) {
                try {
                    initialDelay = convertToMillis(initialDelayString, scheduled.getTimeUnit());
                } catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Invalid initialDelayString value \"" + initialDelayString + stringToLongErrMsg);
                }
            }
        }

        return initialDelay;
    }

    /**
     * 获取对齐时间
     * 用于给 fixedDelay 和 fixedRate 的 initialDelay 做时间对齐
     *
     * @return long
     * @author Silwings
     * @date 2022/6/13 11:31
     * @since 2.10
     */
    private long getAlignTime() {
        Month.of(Calendar.JANUARY).getLastDay(false);

        final long alignTime = DateUtil.getStartOfTime(DateUtil.addMinute(1), Calendar.MINUTE).getTime() - System.currentTimeMillis();
        return alignTime < 0 ? 0 : alignTime;
    }

    /**
     * 该任务信息是否应该刷新
     * 1.该任务在生效中task池中不存在时需要刷新
     * 2.参数任务信息和生效中task池中任务信息不一致且参数任务信息时间戳大于task池中任务信息时间戳时需要刷新
     * 注意!!!该方法不会检查其taskCode对应的任务是否真实存在
     *
     * @param taskInfo 任务信息
     * @return boolean true-需要,false-不需要
     * @author Silwings
     * @date 2022/6/12 17:39
     * @since 2.10
     */
    private boolean needRefresh(final TaskInfo taskInfo) {

        if (null != taskInfo && StringUtils.hasText(taskInfo.getTaskCode())) {

            if (!this.existProcessingTask(taskInfo.getTaskCode())) {
                // 该任务在生效中task池中不存在,需要刷新
                return true;
            }

            final ScheduledTaskInfo inExecutionTask = this.getInExecutionTask(taskInfo.getTaskCode());

            if (taskInfo.newer(inExecutionTask.getTaskInfo())) {
                return true;
            }
        }

        return false;
    }

    private boolean existProcessingTask(final String taskCode) {
        return this.inExecutionScheduledTaskInfoMap.containsKey(taskCode);
    }

    /**
     * 取消定时任务
     *
     * @param taskCode              任务编码
     * @param mayInterruptIfRunning 如果仍在运行，是否强制中断任务（指定 false 以允许任务完成）
     * @return top.silwings.core.ScheduledTaskInfo 被取消的任务
     * @author Silwings
     * @date 2022/6/12 20:44
     * @since 2.10
     */
    protected void cancelTask(final String taskCode, final boolean mayInterruptIfRunning) {
        if (this.existProcessingTask(taskCode)) {
            // 允许正在运行的任务执行完成
            this.inExecutionScheduledTaskInfoMap.get(taskCode).getScheduledTask().cancel(mayInterruptIfRunning);
            this.inExecutionScheduledTaskInfoMap.remove(taskCode);
            log.info("任务 {} 被取消", taskCode);
        }
    }

    private static long convertToMillis(long value, TimeUnit timeUnit) {
        return TimeUnit.MILLISECONDS.convert(value, timeUnit);
    }

    private static boolean isDurationString(String value) {
        return (value.length() > 1 && (isP(value.charAt(0)) || isP(value.charAt(1))));
    }

    private static boolean isP(char ch) {
        return (ch == 'P' || ch == 'p');
    }

    private static long convertToMillis(String value, TimeUnit timeUnit) {
        if (isDurationString(value)) {
            return Duration.parse(value).toMillis();
        }
        return convertToMillis(Long.parseLong(value), timeUnit);
    }

    @Override
    public void setEmbeddedValueResolver(final StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    /**
     * 在spring容器中寻找 taskCode 对应的定时任务的Runnable信息
     *
     * @param taskCode 任务编码
     * @since 2.10
     */
    private Runnable findRunnable(final String taskCode) {

        final ScheduledTaskInfo scheduledTaskInfo = this.getScheduledAnnotationTask(taskCode);

        // 直接取spring容器中的Runnable即可,该Runnable是经过ScheduledAnnotationBeanPostProcessor处理过的包含了aop的代理method.
        // 这里获取到的Runnable真实类型为 ScheduledMethodRunnable ,这里可以直接new一个新的实例,但其内部需要封装的内容和现有的完全一致,所以无需重新封装
        return scheduledTaskInfo.getScheduledTask().getTask().getRunnable();
    }

    protected ScheduledTaskInfo getScheduledAnnotationTask(final String taskCode) {
        final ScheduledTaskInfo scheduledTaskInfo = this.scheduledAnnotationTaskMap.get(taskCode);
        Assert.isTrue(null != scheduledTaskInfo,  "指定任务编码的任务信息不存在! 任务编码: " + taskCode);
        return ScheduledTaskInfo.from(scheduledTaskInfo);
    }

    private void addScheduledAnnotationTask(final ScheduledTaskInfo taskInfo) {
        this.scheduledAnnotationTaskMap.put(taskInfo.getTaskInfo().getTaskCode(), taskInfo);
    }

    /**
     * 获取生效中的定时任务信息
     *
     * @param taskCode 定时任务编码
     * @return top.silwings.task.ScheduledTaskInfo 定时任务信息
     * @author Silwings
     * @date 2022/6/11 22:18
     * @since 2.10
     */
    protected ScheduledTaskInfo getInExecutionTask(final String taskCode) {
        return ScheduledTaskInfo.from(this.inExecutionScheduledTaskInfoMap.get(taskCode));
    }

    /**
     * 向生效中map中添加一个定时任务
     *
     * @param scheduledTaskInfo 定时任务
     * @author Silwings
     * @date 2022/6/12 21:19
     * @since 2.10
     */
    private void addInExecutionTask(final ScheduledTaskInfo scheduledTaskInfo) {
        this.inExecutionScheduledTaskInfoMap.put(scheduledTaskInfo.getTaskInfo().getTaskCode(), scheduledTaskInfo);
    }

    /**
     * 初始化 inExecutionScheduledTaskInfoMap,仅当ScheduledAnnotationTask不存在指定taskCode时才向其中put
     *
     * @param scheduledTaskInfoList 初始化数据源
     * @author Silwings
     * @date 2022/6/12 14:18
     * @since 2.10
     */
    private void initInExecutionScheduledTask(final Collection<ScheduledTaskInfo> scheduledTaskInfoList) {
        if (CollectionUtils.isEmpty(scheduledTaskInfoList)) {
            return;
        }

        for (ScheduledTaskInfo scheduledTaskInfo : scheduledTaskInfoList) {
            final String taskCode = scheduledTaskInfo.getTaskInfo().getTaskCode();
            if (!this.inExecutionScheduledTaskInfoMap.containsKey(taskCode)) {
                this.addInExecutionTask(scheduledTaskInfo);
            }
        }
    }

    /**
     * 初始化 scheduledAnnotationTaskMap,仅当ScheduledAnnotationTask不存在指定taskCode时才向其中put
     *
     * @param scheduledTaskInfoList 初始化数据源
     * @author Silwings
     * @date 2022/6/12 14:14
     * @since 2.10
     */
    private void initScheduledAnnotationTask(final Collection<ScheduledTaskInfo> scheduledTaskInfoList) {

        if (CollectionUtils.isEmpty(scheduledTaskInfoList)) {
            return;
        }

        for (ScheduledTaskInfo scheduledTaskInfo : scheduledTaskInfoList) {
            final String taskCode = scheduledTaskInfo.getTaskInfo().getTaskCode();
            if (!this.scheduledAnnotationTaskMap.containsKey(taskCode)) {
                this.addScheduledAnnotationTask(scheduledTaskInfo);
            }
        }
    }

    @Override
    public void destroy() {

        final Collection<ScheduledTaskInfo> scheduledTaskInfoList = this.inExecutionScheduledTaskInfoMap.values();

        for (ScheduledTaskInfo scheduledTaskInfo : scheduledTaskInfoList) {
            scheduledTaskInfo.getScheduledTask().cancel();
        }

        this.inExecutionScheduledTaskInfoMap.clear();

        this.schedulingConfig.getTaskRegistrar().destroy();
    }

    /**
     * 获取所有生效中定时任务的任务编码
     *
     * @param exceptTaskCodes 需要排除的任务编码
     * @return java.util.Set<java.lang.String> 任务编码集
     * @author Silwings
     * @date 2022/6/13 9:16
     * @since 2.10
     */
    public Set<String> getInExecutionTaskCodes(final String[] exceptTaskCodes) {
        final Set<String> taskCodeSet = new HashSet<>(this.inExecutionScheduledTaskInfoMap.keySet());
        if (ArrayUtil.isNotEmpty(exceptTaskCodes)) {
            for (String exceptTaskCode : exceptTaskCodes) {
                taskCodeSet.remove(exceptTaskCode);
            }
        }
        return taskCodeSet;
    }

    public Set<String> getScheduledAnnotationTaskCodes(final String[] exceptTaskCodes) {
        final Set<String> taskCodeSet = new HashSet<>(this.scheduledAnnotationTaskMap.keySet());
        if (ArrayUtil.isNotEmpty(exceptTaskCodes)) {
            for (String exceptTaskCode : exceptTaskCodes) {
                taskCodeSet.remove(exceptTaskCode);
            }
        }
        return taskCodeSet;
    }


    public Map<String, ScheduledTaskInfo> getInExecutionScheduledTaskInfoMap(){
        return this.inExecutionScheduledTaskInfoMap;
    }

}