package com.haizhi.empower.annotation;

/**
 * @author by fuhanchao
 * @date 2022/12/9.
 */

import com.haizhi.empower.enums.OperationTypeEnum;

import java.lang.annotation.*;

/**
 * <h3>AspectDemo</h3>
 * <p>自定义注解</p>
 * @Target 此注解的作用目标，括号里METHOD的意思说明此注解只能加在方法上面
 * @Retention 注解的保留位置，括号里RUNTIME的意思说明注解可以存在于运行时，可以用于反射
 * @Documented 说明该注解将包含在javadoc中
 * @author : zhang.bw
 * @date : 2020-04-16 14:55
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogDetail {

    /**
     * 方法描述:可使用占位符获取参数:{{tel}}
     */
    String detail() default "";

    /**
     * 日志等级:自己定，此处分为1-9
     */
    int level() default 0;

    /**
     * 操作类型(enum):主要是select,insert,update,delete
     */
    OperationTypeEnum operationType() default OperationTypeEnum.UNKNOWN;



}
