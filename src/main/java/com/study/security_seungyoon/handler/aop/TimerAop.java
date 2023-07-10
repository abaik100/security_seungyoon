package com.study.security_seungyoon.handler.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class TimerAop {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Pointcut("execution(* com.study.security_seungyoon.web.controller..*.*(..))") // 컨트롤러에 있는 모든것을 지정
	private void pointCut() {};
	
	@Pointcut("@annotation(com.study.security_seungyoon.handler.aop.annotation.Timer)")
	private void enableTimer() {};
	
	@Around("pointCut() && enableTimer()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		Object result = joinPoint.proceed();
		
		stopWatch.stop();
		
		LOGGER.info("메소드 실행시간 : {}({}) = {} ({}ms))",
			joinPoint.getSignature().getDeclaringType(),
			joinPoint.getSignature().getName(),
			result, 
			stopWatch.getTotalTimeSeconds());
		return result;
	}
}
