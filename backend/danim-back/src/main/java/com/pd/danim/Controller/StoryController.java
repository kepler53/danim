package com.pd.danim.Controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pd.danim.Form.Request.PhotoPutRequest;
import com.pd.danim.Form.Request.PhotoUploadRequest;
import com.pd.danim.Form.Request.StoryRequest;
import com.pd.danim.Form.Response.PhotoResponse;
import com.pd.danim.Form.Response.StoryDetailResponse;
import com.pd.danim.Service.AuthService;
import com.pd.danim.Service.StoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Story", value = "Story controller")
@RequestMapping("/story")
@RestController
public class StoryController {

	@Autowired
	private StoryService storyService;

	@Autowired
	private AuthService authService;

	@ApiOperation(tags = "사진", value = "사진 업로드", notes = "사진을 업로드합니다")
	@PostMapping("/upload")
	public ResponseEntity<PhotoResponse> photo(@RequestPart("file") MultipartFile file,
			@RequestPart("latitude") String latitude, @RequestPart("longtitude") String longtitude,
			@RequestPart("date") String date, HttpServletRequest httpServletReq) {
		
		PhotoResponse response = new PhotoResponse();
		PhotoUploadRequest photoReq = new PhotoUploadRequest();
		

		response = storyService.uploadPhoto(file,latitude,longtitude,date,httpServletReq);
		
		if(response == null)
			return new ResponseEntity<PhotoResponse>(response, HttpStatus.BAD_REQUEST);

		return new ResponseEntity<PhotoResponse>(response, HttpStatus.OK);
	}

	@ApiOperation(tags = "스토리", value = "스토리 작성", notes = "스토리를 작성 혹은 임시 저장합니다")
	@PostMapping
	public ResponseEntity<Long> postStory(@RequestBody StoryRequest input, HttpServletRequest httpServletReq) {

		long response = storyService.writeStory(input, httpServletReq);
		
		if(response == -1) {
			return new ResponseEntity<Long>(response, HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Long>(response, HttpStatus.OK);
	}

	@ApiOperation(tags = "스토리", value = "스토리 조회", notes = "스토리 내용을 조회합니다")
	@GetMapping("/{storyno}")
	public ResponseEntity<StoryDetailResponse> getStory(@PathVariable("storyno") long storyno, HttpServletRequest httpServletReq) {
		// 조회
		
		StoryDetailResponse storyDetail = storyService.getStory(storyno, httpServletReq);

		if(storyDetail == null)
			return new ResponseEntity<StoryDetailResponse>(storyDetail, HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<StoryDetailResponse>(storyDetail, HttpStatus.OK);
	}

	@ApiOperation(tags = "스토리", value = "스토리 수정", notes = "스토리 내용을 수정합니다")
	@PutMapping("/{storyno}")
	public ResponseEntity<String> putStory(@PathVariable("storyno") long storyNo, @RequestBody StoryRequest req, HttpServletRequest httpServletReq) {

		int res= storyService.modifyStory(storyNo, req, httpServletReq);
		if(res == 401)
			return new ResponseEntity<String>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
		else if(res == 404)
			return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND); 
		else if(res== 406)
			return new ResponseEntity<String>("NOT ACCEPTABLE", HttpStatus.NOT_ACCEPTABLE);
		
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

	@ApiOperation(tags = "스토리", value = "사진 수정", notes = "사진 내용을 수정합니다")
	@PutMapping("/photo")
	public ResponseEntity<String> putPhoto(@RequestBody PhotoPutRequest req, HttpServletRequest httpServletReq) {
	
		int res= storyService.modifyPhoto(req, httpServletReq);
		if(res == 401)
			return new ResponseEntity<String>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
		else if(res == 404)
			return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND); 
		else if(res== 406)
			return new ResponseEntity<String>("NOT ACCEPTABLE", HttpStatus.NOT_ACCEPTABLE);
		
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}
	
	@ApiOperation(tags = "스토리", value = "스토리 삭제", notes = "스토리를 삭제합니다")
	@DeleteMapping("/{storyno}")
	public ResponseEntity<String> deleteStory(@PathVariable("storyno") long storyNo, HttpServletRequest httpServletReq) {
		
		int res = storyService.deleteStory(storyNo, httpServletReq);
		
		if(res == 401)
			return new ResponseEntity<String>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
		else if(res == 404)
			return new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND); 
		else if(res== 406)
			return new ResponseEntity<String>("NOT ACCEPTABLE", HttpStatus.NOT_ACCEPTABLE);
			

		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

}
