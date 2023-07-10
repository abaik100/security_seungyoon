package com.study.security_seungyoon.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.study.security_seungyoon.service.notice.NoticeService;
import com.study.security_seungyoon.web.dto.CMRespDto;
import com.study.security_seungyoon.web.dto.notice.AddNoticeReqDto;
import com.study.security_seungyoon.web.dto.notice.GetNoticeRespDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/notice")
@Slf4j
@RequiredArgsConstructor
public class NoticeRestController {
	
	private final NoticeService noticeService;
	
	@Value("${file.path}")
	private String filePath;
	
	@PostMapping("")
	public ResponseEntity<?> addNotice(AddNoticeReqDto addNoticeReqDto) {
//		log.info(">>>{}:", addNoticeReqDto);
//		log.info(">>> fileName: {}", addNoticeReqDto.getFile().get(0).getOriginalFilename());
//		log.info("filePath: {}", filePath);
		
		int noticeCode = 0;
		try {
			noticeCode = noticeService.addNotice(addNoticeReqDto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new CMRespDto<>(-1, "Failed", noticeCode));
		}
		
		return ResponseEntity.ok().body(new CMRespDto<>(1, "complete creation", noticeCode));
	}
	
	@GetMapping("/{noticeCode}")
	public ResponseEntity<?> getNotice(@PathVariable int noticeCode) {
		
		GetNoticeRespDto getNoticeRespDto = null;
		try {
			getNoticeRespDto = noticeService.getNotice(null, noticeCode);
			if(getNoticeRespDto == null) {
				return ResponseEntity.badRequest().body(new CMRespDto<>(-1, "datebase failed", null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new CMRespDto<>(-1, "datebase error", null));
		}
		return ResponseEntity.ok().body(new CMRespDto<>(1, "success", getNoticeRespDto));
	}
	
	@GetMapping("/{flag}/{noticeCode}")
	public ResponseEntity<?> getNotice(@PathVariable String flag, @PathVariable int noticeCode) {
		GetNoticeRespDto getNoticeRespDto = null;
		if(flag.equals("pre") || flag.equals("next")) {
			try {
				getNoticeRespDto = noticeService.getNotice(flag, noticeCode);
				if(getNoticeRespDto == null) {
					return ResponseEntity.badRequest().body(new CMRespDto<>(-1, "datebase failed", null));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.internalServerError().body(new CMRespDto<>(-1, "datebase error", null));
			}			
		}else {
			return ResponseEntity.ok().body(new CMRespDto<>(1, "request failed", null));
		}
		return ResponseEntity.ok().body(new CMRespDto<>(1, "success", getNoticeRespDto));
	}
	
}







