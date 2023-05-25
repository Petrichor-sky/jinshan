package com.haizhi.empower.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author: lzp
 * @description:
 * @Date: 2022/8/6
 */
public class DateUtils {

    /**
     * 一天、一分钟、一小时对应的秒数
     */
    private static final Long ONE_MINUTE_TO_SECOND = 60L;
    private static final Long ONE_HOUR_TO_SECOND = ONE_MINUTE_TO_SECOND * 60;
    private static final Long ONE_DAY_TO_SECOND = ONE_HOUR_TO_SECOND * 24;

    /**
     * 使用LocalDateTime进行格式化 保证多线程安全
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER2 = DateTimeFormatter.ofPattern("MM-dd");

    /**
     * 设置推送消息的时间描述
     * 1. 1分钟内：刚刚
     * 2. 60分钟内：x分钟前
     * 3. 24小时内：x小时前
     * 4. >24小时：x月x日  08-1
     * 5. 非年内：年-月-日 2021-05-02
     *
     * @param now        当前时间  传一个当前时间目的是方便遍历，减少date对象的创建
     * @param targetDate 与当前时间做对比的时间
     */
    public static String getTimeDescriptionByDate(Date now, Date targetDate) {
        String timeDescription = "";
        if (targetDate != null) {
            // 5. 年内判断
            if (targetDate.getYear() == now.getYear()) {
                // 获取秒数差
                long betweenSeconds = (now.getTime() - targetDate.getTime()) / 1000;
                if (betweenSeconds < ONE_MINUTE_TO_SECOND) {
                    // 1. 1分钟内：刚刚
                    timeDescription = "刚刚";
                } else if (betweenSeconds < ONE_HOUR_TO_SECOND) {
                    // 2. 60分钟内
                    timeDescription = betweenSeconds / ONE_MINUTE_TO_SECOND + "分钟前";
                } else if (betweenSeconds < ONE_HOUR_TO_SECOND * 24) {
                    // 3. 24小时内：x小时前
                    timeDescription = betweenSeconds / ONE_HOUR_TO_SECOND + "小时前";
                } else {
                    // 4. >24小时：x月x日  08-1
                    timeDescription = dateToLocalDateTime(targetDate).format(DATE_TIME_FORMATTER2);
                }
            } else {
                timeDescription = dateToLocalDateTime(targetDate).format(DATE_TIME_FORMATTER1);
            }
        }
        return timeDescription;
    }

    /**
     * date转localDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}

