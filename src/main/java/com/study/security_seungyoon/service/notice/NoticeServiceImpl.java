package com.study.security_seungyoon.service.notice;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.study.security_seungyoon.domain.notice.Notice;
import com.study.security_seungyoon.domain.notice.NoticeFile;
import com.study.security_seungyoon.domain.notice.NoticeRepository;
import com.study.security_seungyoon.web.dto.notice.AddNoticeReqDto;
import com.study.security_seungyoon.web.dto.notice.GetNoticeRespDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{
	
	@Value("${file.path}")
	private String filePath;
	
	private final NoticeRepository noticeRepository;
	
	@Override
	public int addNotice(AddNoticeReqDto addNoticeReqDto) throws Exception {
		
		Predicate<String> predicate = (filename) -> !filename.isBlank();
		
		Notice notice = null;
		
		notice = Notice.builder()
				.notice_title(addNoticeReqDto.getNoticeTitle())
				.user_code(addNoticeReqDto.getUserCode())
				.notice_content(addNoticeReqDto.getIr1())
				.build();
		
		noticeRepository.saveNotice(notice);
		
		if(predicate.test(addNoticeReqDto.getFile().get(0).getOriginalFilename())) {
			
			List<NoticeFile> noticeFiles = new ArrayList<NoticeFile>();
			
			for(MultipartFile file : addNoticeReqDto.getFile()) {
				String originFilename = file.getOriginalFilename();
				String tempFilename = UUID.randomUUID().toString().replaceAll("-", "") + "_" + originFilename;
				
				Path uploadPath = Paths.get(filePath, "notice/" + tempFilename);
				
				File f = new File(filePath + "notice");
				if(!f.exists()) {
					f.mkdir();
				}
					
				Files.write(uploadPath, file.getBytes());
				
				noticeFiles.add(NoticeFile.builder()
										.notice_code(notice.getNotice_code())
										.file_name(tempFilename)
										.build());
				
				
				}
				noticeRepository.saveNoticeFiles(noticeFiles);
			};
		return notice.getNotice_code();
	}
	
	@Override
	public GetNoticeRespDto getNotice(String flag, int noticeCode) throws Exception {
		GetNoticeRespDto getNoticeRespDto = null;
		
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("flag", flag);
		reqMap.put("notice_code", noticeCode);
		
		List<Notice> notices = noticeRepository.getNotice(reqMap);
		if(!notices.isEmpty()) {
			List<Map<String, Object>> downloadFiles = new ArrayList<Map<String, Object>>();
			notices.forEach(notice -> {
				Map<String, Object> fileMap = new HashMap<String, Object>();
				fileMap.put("fileCode", notice.getFile_code());
				String fileName = notice.getFile_name();
				if(fileName != null) {
					fileMap.put("fileCode", notice.getFile_code());
					fileMap.put("fileOriginName", fileName.substring(fileName.indexOf("_") + 1));
					fileMap.put("fileTempName", fileName);
				}
				
				downloadFiles.add(fileMap);	
			});
			
			Notice firstNotice = notices.get(0);
			getNoticeRespDto = GetNoticeRespDto.builder()
					.noticeCode(firstNotice.getNotice_code())
					.noticeTitle(firstNotice.getNotice_title())
					.userCode(firstNotice.getUser_code())
					.userId(firstNotice.getUser_id())
					.createDate(firstNotice.getCreate_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
					.noticeCount(firstNotice.getNotice_count())
					.noticeContent(firstNotice.getNotice_content())
					.downloadFiles(downloadFiles)
					.build();
		}
		return getNoticeRespDto;
	}

}







