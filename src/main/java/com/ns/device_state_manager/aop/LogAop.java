package com.ns.device_state_manager.aop;


import com.alibaba.fastjson2.JSON;
import com.ns.device_state_manager.utils.UUIDUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class LogAop {
    private static final Logger logger = LoggerFactory.getLogger(LogAop.class);

    /*
     * 指定切点
     */
    @Pointcut("execution(public * com.ns.device_state_manager.controller.*.*(..))")
    public void webLog() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MDC.clear();
        MDC.put("LOGID", UUIDUtil.getUUID());
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String remoteIp = request.getRemoteAddr();
        Object[] args = pjp.getArgs();
        StringBuilder params = new StringBuilder();
        //获取请求参数集合并进行遍历拼接
        if (args.length > 0) {
            if ("POST".equals(method)) {
                for (Object o : args) {
                    params.append(String.format("%s\n", JSON.toJSON(o)));
                }
            } else if ("GET".equals(method)) {
                params.append(queryString);
            }
        }

        logger.info("remoteIp:" + remoteIp);
        logger.info("url:" + url);
        logger.info("method:" + method);
        logger.info("params:" + params);
        // result的值就是被拦截方法的返回值
        Object result = pjp.proceed();
        logger.info("return:" + JSON.toJSON(result));
        return result;
    }


}
