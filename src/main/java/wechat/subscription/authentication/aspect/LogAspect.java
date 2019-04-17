package wechat.subscription.authentication.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LogAspect {
    Logger logger = LoggerFactory.getLogger(LogAspect.class);

    //controller aspect log
    @Pointcut("execution(* wechat.subscription.authentication.controller.*.*(..))")
    public void LogAspectController(){

    }

    @Before("LogAspectController()")
    public void controllerRequestBefroe(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("URL : " + request.getRequestURL().toString());
        logger.info("HTTP_METHOD : " + request.getMethod());
        logger.info("IP : " + request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "LogAspectController()",returning = "returnValue")
    public void controllerRequestAfter(Object returnValue){
        logger.info("RESPONSE:"+returnValue.toString());
    }

    @Pointcut("execution(* wechat.subscription.authentication.service.*.*(..))")
    public void LogAspectService(){

    }

    @Before("LogAspectService()")
    public void serviceExecuteBefore(JoinPoint joinPoint){
        Object[] objs= joinPoint.getArgs();
        String[] argsName = ((MethodSignature)joinPoint.getSignature()).getParameterNames();

        int length = Math.min(objs.length,argsName.length);
        logger.info("EXECUTE METHOD : "+joinPoint.getSignature().getName());
        for(int i = 0 ; i < length;i++){
            logger.info("PARAM NAME : "+argsName[i]+" ;SPARAM VALUE : " + objs[i]);
        }

       /* logger.info("joinPoint.getKind():"+joinPoint.getKind());
        logger.info("joinPoint.getTarget():"+joinPoint.getTarget().toString());
        logger.info("joinPoint.getThis()"+joinPoint.getThis().toString());

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        String[] strings = signature.getParameterNames();
        for(int i = 0 ;i < strings.length ;i++){
            logger.info("signature.getParameterNames() get"+i+" : " +strings[i]);
        }
        logger.info("signature.getDeclaringTypeName()"+signature.getDeclaringTypeName());
        logger.info("signature.getDeclaringType()"+signature.getDeclaringType().toGenericString());
        logger.info("signature.getName()"+signature.getName());*/
       /* 2019-04-17 11:09:02.835  INFO 7524 --- [nio-8080-exec-4] w.s.authentication.aspect.LogAspect      : joinPoint.getArgs() return Objetct[0]:20_3RgGzEuvJ2sg42VYcF9qIFZWqK-NTdtDTaPAhB0LPjZrmnp7wWV2zvF-JG6_zQizI80X8g1YmMNyQNKblJZVfzyvid7B9oipZ4a2odo-yBAdoWIp_5C0eHKr-e1KXEJOsmLrRT1fIMSLSC5bPGTbAAAZQD
        2019-04-17 11:09:02.835  INFO 7524 --- [nio-8080-exec-4] w.s.authentication.aspect.LogAspect      : joinPoint.getKind():method-execution
        2019-04-17 11:09:02.835  INFO 7524 --- [nio-8080-exec-4] w.s.authentication.aspect.LogAspect      : joinPoint.getTarget():wechat.subscription.authentication.service.WechatService@6244731d
        2019-04-17 11:09:02.835  INFO 7524 --- [nio-8080-exec-4] w.s.authentication.aspect.LogAspect      : joinPoint.getThis()wechat.subscription.authentication.service.WechatService@6244731d
        2019-04-17 11:09:02.835  INFO 7524 --- [nio-8080-exec-4] w.s.authentication.aspect.LogAspect      : signature.getParameterNames() get0 : accessToken
        2019-04-17 11:09:02.835  INFO 7524 --- [nio-8080-exec-4] w.s.authentication.aspect.LogAspect      : signature.getDeclaringTypeName()wechat.subscription.authentication.service.WechatService
        2019-04-17 11:09:02.835  INFO 7524 --- [nio-8080-exec-4] w.s.authentication.aspect.LogAspect      : signature.getDeclaringType()public class wechat.subscription.authentication.service.WechatService
        2019-04-17 11:09:02.835  INFO 7524 --- [nio-8080-exec-4] w.s.authentication.aspect.LogAspect      : signature.getName()getQRCode*/
    }

    @AfterReturning(pointcut = "LogAspectService()",returning = "returnValue")
    public void serviceExecuteAfter(Object returnValue){
        logger.info(" RETURN VALUE : "+returnValue.toString());
    }
}
