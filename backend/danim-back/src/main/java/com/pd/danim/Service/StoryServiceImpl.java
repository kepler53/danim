package com.pd.danim.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.pd.danim.DTO.DanimId;
import com.pd.danim.DTO.Photo;
import com.pd.danim.DTO.Place;
import com.pd.danim.DTO.Story;
import com.pd.danim.DTO.SubStory;
import com.pd.danim.DTO.User;
import com.pd.danim.Form.Request.PhotoRequest;
import com.pd.danim.Form.Request.StoryRequest;
import com.pd.danim.Form.Response.PhotoResponse;
import com.pd.danim.Repository.DanimRepository;
import com.pd.danim.Repository.PhotoRepository;
import com.pd.danim.Repository.PlaceRepository;
import com.pd.danim.Repository.StoryRepository;
import com.pd.danim.Repository.SubStoryRepository;
import com.pd.danim.Repository.UserRepository;
import com.pd.danim.Util.GoogleReverseGeocodeUtil;
import com.pd.danim.Util.JwtUtil;

@Service
public class StoryServiceImpl implements StoryService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private DanimRepository danimRepo;

	@Autowired
	private PhotoRepository photoRepo;

	@Autowired
	private SubStoryRepository subStoryRepo;

	@Autowired
	private StoryRepository storyRepo;
	 
	@Autowired
	private PlaceRepository placeRepo;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private GoogleReverseGeocodeUtil geocodeUtil;
	
	public PhotoResponse uploadPhoto(MultipartFile mfile,String latitude, String longtitude, String date, HttpServletRequest httpServletReq) {
		
		final String requestTokenHeader = httpServletReq.getHeader("Authorization");
		String userId = jwtUtil.getUsername(requestTokenHeader);
		
		DanimId danim = danimRepo.findById(userId);
		if(danim==null) {
			return null;			
		}
		
		UUID uid = UUID.randomUUID();
		String absolutePath = new File("").getAbsolutePath() + File.separator;
		String path = "src" +  File.separator + "main" +  File.separator + "resources" +  File.separator + "danim-image" + File.separator + danim.getUser().getNickname();
		File file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}

		String originalFileExtension;
		String contentType = mfile.getContentType();

	
		if (ObjectUtils.isEmpty(contentType)) {
			return null;
		}

		
		if (contentType.contains("image/jpeg")) {
			originalFileExtension = ".jpg";
		} else if (contentType.contains("image/png")) {
			originalFileExtension = ".png";
		} else {
			return null;
		}
		
		String address = geocodeUtil.getAddress(latitude, longtitude);
		Place place = placeRepo.findByAddress(address);
		String placeName = null;
		if(place!=null) {
			placeName = place.getName();
		}
	
		
		String filename = uid.toString() + originalFileExtension;

		// 파일 저장
		file = new File(absolutePath + path + File.separator + filename);
		file.setWritable(true);
		file.setReadable(true);
		try {
			mfile.transferTo(file);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
		
		PhotoResponse response = new PhotoResponse();
		response.setFilename(filename);
		response.setDate(LocalDateTime.parse(date,formatter));
		response.setAddress(address);
		response.setPlaceName(placeName);
		response.setLatitude(latitude);
		response.setLongtitude(longtitude);
		
		
		return response;
	}

	@Override
	public boolean writeStory(StoryRequest input, HttpServletRequest httpServletReq) {

		final String requestTokenHeader = httpServletReq.getHeader("Authorization");
		String userId = jwtUtil.getUsername(requestTokenHeader);
		
		DanimId danim = danimRepo.findById(userId);
		if(danim==null) {
			return false;			
		}
		long userno = danim.getUserno();
		Story story = new Story();

		List<PhotoRequest> photoReqList = input.getPhotos();


		SubStory[] subStoryArr = new SubStory[input.getDuration()];

		story.setCreatedDate(LocalDateTime.now());
		story.setStartDate(input.getStartDate());
		story.setDuration(input.getDuration());
		story.setStatus(input.getStatus());
		story.setTitle(input.getTitle());

		int seqNo = 0;
		List<Photo> photoList = new ArrayList();
		for (PhotoRequest photoReq : photoReqList) {
			Photo photo = new Photo(); 

			
			photo.setFilename(photoReq.getFilename());
			photo.setLatitude(photoReq.getLatitude());
			photo.setLongtitude(photoReq.getLongtitude());
			photo.setDate(LocalDateTime.parse(photoReq.getDate()));		
			photo.setAddress(photoReq.getAddress());
			photo.setPlaceName(photoReq.getPlaceName());
			photo.setContent(photoReq.getContent());
			
			seqNo = (int) Duration.between(input.getStartDate(), photo.getDate()).toDays();
			subStoryArr[seqNo].setUserNo(userno);
			subStoryArr[seqNo].setSeqNo(seqNo);
			subStoryArr[seqNo].setStory(story);
			photo.setStory(story);
			photo.setSubstory(subStoryArr[seqNo]);
			photo.setUserNo(userno);
			
			photoList.add(photo);
		}

		// 스토리 저장
		storyRepo.save(story);

		// 서브 스토리 저장
		for (SubStory sub : subStoryArr) {
			subStoryRepo.save(sub);
		}

		// 포토 저장
		for (Photo photo : photoList) {
			photoRepo.save(photo);
		}

		return true;
	}

	@Override
	public boolean modifyStory(StoryRequest input, long storyno) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getStory(long storyno) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getStories(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteStory(long storyno) {
		// TODO Auto-generated method stub
		return false;
	}

}
