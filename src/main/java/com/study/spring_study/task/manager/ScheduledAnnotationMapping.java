package com.study.spring_study.task.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName ScheduledAnnotationMapping
 * @Description Scheduled 注解的映射类
 * @Author Silwings
 * @Date 2022/6/14 15:22
 * @Version V1.0
 **/
@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
@Accessors(chain = true)
public class ScheduledAnnotationMapping {

    private String cron = "";

    private String zone = "";

    private long fixedDelay = -1L;

    private String fixedDelayString = "";

    private long fixedRate = -1L;

    private String fixedRateString = "";

    private long initialDelay = -1L;

    private String initialDelayString = "";

    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    public static ScheduledAnnotationMapping from(final Scheduled scheduled) {

        final ScheduledAnnotationMapping mapping = new ScheduledAnnotationMapping();
        mapping.setCron(scheduled.cron())
                .setZone(scheduled.zone())
                .setFixedDelay(scheduled.fixedDelay())
                .setFixedDelayString(scheduled.fixedDelayString())
                .setFixedRate(scheduled.fixedRate())
                .setFixedRateString(scheduled.fixedRateString())
                .setInitialDelay(scheduled.initialDelay())
                .setInitialDelayString(scheduled.initialDelayString())
                .setTimeUnit(scheduled.timeUnit());

        return mapping;
    }

    public static ScheduledAnnotationMapping from(final ScheduledAnnotationMapping scheduledAnnotationMapping) {
        final ScheduledAnnotationMapping mapping = new ScheduledAnnotationMapping();
        mapping.setCron(scheduledAnnotationMapping.getCron())
                .setZone(scheduledAnnotationMapping.getZone())
                .setFixedDelay(scheduledAnnotationMapping.getFixedDelay())
                .setFixedDelayString(scheduledAnnotationMapping.getFixedDelayString())
                .setFixedRate(scheduledAnnotationMapping.getFixedRate())
                .setFixedRateString(scheduledAnnotationMapping.getFixedRateString())
                .setInitialDelay(scheduledAnnotationMapping.getInitialDelay())
                .setInitialDelayString(scheduledAnnotationMapping.getInitialDelayString())
                .setTimeUnit(scheduledAnnotationMapping.getTimeUnit());

        return mapping;
    }



    public boolean same(final ScheduledAnnotationMapping scheduledAnnotationMapping) {
        return this.getCron().equals(scheduledAnnotationMapping.getCron())
                && this.getZone().equals(scheduledAnnotationMapping.getZone())
                && this.getFixedDelay() == scheduledAnnotationMapping.getFixedDelay()
                && this.getFixedDelayString().equals(scheduledAnnotationMapping.getFixedDelayString())
                && this.getFixedRate() == scheduledAnnotationMapping.getFixedRate()
                && this.getFixedRateString().equals(scheduledAnnotationMapping.getFixedRateString())
                && this.getInitialDelay() == scheduledAnnotationMapping.getInitialDelay()
                && this.getInitialDelayString().equals(scheduledAnnotationMapping.getInitialDelayString())
                && this.getTimeUnit().equals(scheduledAnnotationMapping.getTimeUnit());
    }

    public boolean different(final ScheduledAnnotationMapping scheduledAnnotationMapping) {
        return !this.same(scheduledAnnotationMapping);
    }
}
