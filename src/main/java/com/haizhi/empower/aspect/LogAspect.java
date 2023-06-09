package com.haizhi.empower.aspect;

/**
 * @author by fuhanchao
 * @date 2022/12/9.
 */

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.empower.annotation.OperationLogDetail;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.entity.po.OperationLog;
import com.haizhi.empower.service.OperationLogService;
import com.haizhi.empower.util.RedisUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <h3>AspectDemo</h3>
 * <p>AOP切面类</p>
 *
 * @author : zhang.bw
 * @date : 2020-04-16 14:52
 **/
/*@Aspect
@Component*/
public class LogAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    @Value("${AppName:投票星}")
    private  String  AppName;

    @Value("${tokenName}")
    String tokenName;

    @Resource
    OperationLogService operationLogService;

    @Autowired
    RedisUtils redisUtils;

    /**
     * 定义切点
     * 此处的切点是注解的方式，也可以用包名的方式达到相同的效果
     * '@Pointcut("execution(* com.bowen.service.impl.*.*(..))")'
     */
    @Pointcut("execution(* com.haizhi.empower.service.*.*(..))")
    public void operationLog() {
    }


    /**
     * 环绕增强，相当于MethodInterceptor
     */
    @Around("operationLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = null;
        long time = System.currentTimeMillis();
        try {
            res = joinPoint.proceed();
            time = System.currentTimeMillis() - time;
            return res;
        } finally {
            try {
                //方法执行完成后增加日志
                addOperationLog(joinPoint, res, time+"ms");
            } catch (Exception e) {
                LOG.error("LogAspect 操作失败：" + e.getMessage());
            }
        }
    }

    /**
     * 方法执行完成后增加日志
     *
     * @param joinPoint
     * @param res
     * @param time
     */
    private void addOperationLog(JoinPoint joinPoint, Object res, String time) {
        LOG.info("开始记录日志...");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog operationLog = new OperationLog();
        operationLog.setRunTime(time);
        operationLog.setId(UUID.randomUUID().toString());
        operationLog.setArgs(Arrays.toString(joinPoint.getArgs()));
        operationLog.setCreateTime(new Date());
        operationLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        LOG.info("执行method：{}", operationLog.getMethod());
        operationLog.setReturnValue(res.toString());
        OperationLogDetail annotation = null;
        try {
            Method method = signature.getMethod();
            Class<?> clazz = joinPoint.getTarget().getClass();
            method = clazz.getMethod(method.getName(), method.getParameterTypes());
            annotation = method.getAnnotation(OperationLogDetail.class);
        } catch (Exception e) {
            LOG.error("获取当前类有 OperationLogDetail 注解的方法 异常", e);
        }
        if (annotation != null) {
            operationLog.setLevel(annotation.level());

                operationLog.setDescribes(annotation.detail()+"-"+AppName);

            if ("login".equals(request.getHeader("method"))) {
                JSONObject data=(JSONObject)((ObjectRestResponse) res).getData();
                operationLog.setUserId(data.getJSONObject("user_info").getString("username"));
                operationLog.setUserName(data.getJSONObject("user_info").getString("contact"));

            } else {
                String accessToken=request.getHeader("access_token");
                JSONObject data=(JSONObject)redisUtils.get(tokenName+":"+accessToken);
                operationLog.setUserId(data.getJSONObject("user_info").getString("username"));
                operationLog.setUserName(data.getJSONObject("user_info").getString("contact"));

            }
            operationLog.setOperationType(annotation.operationType().getValue());
            //TODO 这里保存日志
            operationLogService.insertSelective(operationLog);
            LOG.info("记录日志:" + operationLog);
        }
        LOG.info("记录日志结束...");
    }

    /**
     * 对当前登录用户和占位符处理
     *
     * @param argNames   方法参数名称数组
     * @param args       方法参数数组
     * @param annotation 注解信息
     * @return 返回处理后的描述
     */
    @Deprecated
    private String getDetail(String[] argNames, Object[] args, OperationLogDetail annotation) {

        Map<Object, Object> map = new HashMap<>(4);
        for (int i = 0; i < argNames.length; i++) {
            map.put(argNames[i], args[i]);
        }

        String detail = annotation.detail();
        try {
            detail = "'" + "#{currentUserName}" + "'=》" + annotation.detail();
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                detail = detail.replace("{{" + k + "}}", JSON.toJSONString(v));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Before("operationLog()")
    public void doBeforeAdvice(JoinPoint joinPoint) {
        LOG.info("进入方法前执行.....");
    }

    /**
     * 处理完请求，返回内容
     *
     * @param ret
     */
    @AfterReturning(returning = "ret", pointcut = "operationLog()")
    public void doAfterReturning(Object ret) {
        LOG.info("方法的返回值 : " + ret);
    }

    /**
     * 后置异常通知
     */
    @AfterThrowing("operationLog()")
    public void throwss(JoinPoint jp) {
        LOG.info("方法异常时执行.....");
    }

    /**
     * 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
     */
    @After("operationLog()")
    public void after(JoinPoint jp) {
        LOG.info("方法最后执行.....");
    }
}
