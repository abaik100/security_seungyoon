package com.study.security_seungyoon.handler.aop;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.study.security_seungyoon.handler.exception.CustomValidationApiException;
import com.study.security_seungyoon.web.dto.CMRespDto;

@Aspect
@Component
public class ValidationAop {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Pointcut("@annotation(com.study.security_seungyoon.handler.aop.annotation.ValidCheck)")
	private void enableValid() {
	};
	@Before("enableValid()")
	   public void ValidBefore(JoinPoint joinpoint) {
	      
	      Object[] args = joinpoint.getArgs();
	      LOGGER.info(">>>> 유효성 검사중... <<<<");
	      
	      for(Object arg : args) {
	         if(arg.getClass() == BeanPropertyBindingResult.class) {
	            BindingResult bindingResult = (BindingResult)arg;
	                  
	                  if(bindingResult.hasErrors()){
	                        
	                        Map<String, String> errorMessage = new HashMap<String, String>();
	                        
	                        bindingResult.getFieldErrors().forEach(error -> {
	                           System.out.println("오류발생 필드명 :" + error.getField());
	                           System.out.println("오류발생 상세 메시지:" + error.getDefaultMessage());
	                           errorMessage.put(error.getField(),error.getDefaultMessage());
	                        });
	                        throw new CustomValidationApiException("유효성 검사 실패", errorMessage);
	                     }
	         }
	      }
	   }
	   
	   @AfterReturning(value = "enableValid()", returning = "returnObj")
	   public void afterReturn(JoinPoint joinPoint, Object returnObj) {
	      LOGGER.info("유효성 검사 완료: {}", returnObj);
	   }

	
}