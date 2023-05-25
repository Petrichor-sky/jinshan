package com.haizhi.empower.base;

/**
 * 研判中心-常量
 */
public interface JudgeTaskConstants {

    /**
     * 研判中心-常量-异常
     */
    final class ServiceError {
        //登录人唯一标识异常
        public static final String LOGIN_USER_CODE_ERROR =  "登录人唯一标识异常";
        //任务主键异常参数
        public static final int TASK_PRIMARY_KEY_ERROR_PARAM = 0;
    }

    /**
     * 研判中心-常量-业务
     */
    final class ServiceParam {
        //进行中的任务状态
        public static final String PROGRESS_TASK_STATUS = "0";
        //已完成的任务状态
        public static final String FINISH_TASK_STATUS = "1";
        //首页待办任务初始化展示个数
        public static final int PEND_TASK_INIT_SHOW_COUNT = 0;
        //首页待办任务列表展示条数
        public static final long PEND_TASK_LIST_SHOW_ROW = 7L;
        //首页消息通知展示条数
        public static final long MESSAGE_SHOW_ROW = 10L;
        //分页请求非法参数
        public static final int PAGE_NUM_AND_PAGE_SIZE_ERROR_PARAM = 0;
        //是当前登录人创建的任务
        public static final int IS_LOGIN_USER_CREATE_TASK = 1;
        //不是当前登录人创建的任务
        public static final int NOT_LOGIN_USER_CREATE_TASK = 0;
        //进行中的任务数量key
        public static final String PROGRESS_COUNT_KEY = "progress";
        //已完成的任务数量key
        public static final String FINISH_COUNT_KEY = "finish";
        //我创建的任务数量key
        public static final String MY_CREATE_COUNT = "myCreate";
        //任务计数默认值
        public static final Integer TASK_DEFAULT_COUNT = 0;
        //空字符串
        public static final String BLANK_STRING = "";
        //空格字符串
        public static final String SPACE_STRING = " ";
        //研判任务表名
        public static final String JUDGE_TASK_TABLE_NAME = "t_judge_task";
        //研判任务协同表名
        public static final String JUDGE_TASK_COORDINATION_TABLE_NAME = "t_judge_task_coordination";
        //关联要素:警情
        public static final String INFORMING_EVENT_TYPE = "informing";
        //关联要素:案件
        public static final String CASE_EVENT_TYPE = "case";
        //关联要素:人员
        public static final String MAN_EVENT_TYPE = "man";
        //关联要素:车辆
        public static final String VEHICLE_EVENT_TYPE = "vehicle";
        //任务事件map初始化容量
        public static final int TASK_EVENT_MAP_DEFAULT_INIT_VOLUME = 1;
        //任务一级协同父类id
        public static final int FIRST_ORDER_COORDINATION_PARENT_ID = 0;
    }
}
