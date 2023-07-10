package com.study.security_seungyoon.web.controller.api;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.security_seungyoon.handler.aop.annotation.Timer;
import com.study.security_seungyoon.handler.aop.annotation.ValidCheck;
import com.study.security_seungyoon.handler.aop.annotation.Log;
import com.study.security_seungyoon.handler.exception.CustomValidationApiException;
import com.study.security_seungyoon.service.auth.AuthService;
import com.study.security_seungyoon.service.auth.PrincipalDetails;
import com.study.security_seungyoon.service.auth.PrincipalDetailsService;
import com.study.security_seungyoon.web.dto.CMRespDto;
import com.study.security_seungyoon.web.dto.auth.SignupReqDto;
import com.study.security_seungyoon.web.dto.auth.UsernameCheckReqDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final PrincipalDetailsService principalDetailsService;
	private final AuthService authService;
	
	@ValidCheck
	@PostMapping("/signup")
	public ResponseEntity<?> sign(@RequestBody @Valid SignupReqDto signupReqDto, BindingResult bindingResult){

		
		boolean status = false;
		try {
			principalDetailsService.addUser(signupReqDto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok().body(new CMRespDto<>(-1,"회원가입 실패",status));
		}
		return ResponseEntity.ok().body(new CMRespDto<>(1,"회원가입 성공",status));
	}
	
	@ValidCheck
	@Log
	@Timer
	@GetMapping("/signup/validation/username")
	public ResponseEntity<?> checkUsername(@Valid UsernameCheckReqDto usernameCheckReqDto, BindingResult bindingResult){
		

		boolean status = false;
		
		try {
			status = authService.checkUsername(usernameCheckReqDto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(new CMRespDto<>(1, "서버오류", status));
		}
		
		return ResponseEntity.ok().body(new CMRespDto<>(1, "회원가입 가능 여부", status));
	}
	
	@GetMapping("/principal")
	public ResponseEntity<?> getPrincipal(@AuthenticationPrincipal PrincipalDetails principalDetails){
		if(principalDetails == null) {
			return ResponseEntity.badRequest().body(new CMRespDto<>(-1, "principal is null", null));
		}
		return ResponseEntity.ok(new CMRespDto<>(1, "success load",principalDetails.getUser() )); //javascript 가 띄운다
	}
}


