package com.pd.danim.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pd.danim.DTO.DanimId;
import com.pd.danim.DTO.Interest;
import com.pd.danim.DTO.Photo;
import com.pd.danim.DTO.Story;
import com.pd.danim.DTO.User;
import com.pd.danim.Form.Response.StoryResponse;
import com.pd.danim.Repository.DanimRepository;
import com.pd.danim.Repository.PhotoRepository;
import com.pd.danim.Repository.StoryRepository;
import com.pd.danim.Repository.UserRepository;
import com.pd.danim.Util.JwtUtil;

@Service
public class HomeServiceImpl implements HomeService {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private DanimRepository danimRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private StoryRepository storyRepository;

	@Override
	public List<List<StoryResponse>> getMyPopularStory(HttpServletRequest httpServletRequest) {

		final String requestTokenHeader = httpServletRequest.getHeader("Authorization");
		String userId = jwtUtil.getUsername(requestTokenHeader);

		DanimId danim = danimRepository.findById(userId);
		User user = danim.getUser();

		List<Interest> interests = user.getInterests();
		List<List<StoryResponse>> storyResponses = new ArrayList<List<StoryResponse>>();

		for (Interest interest : interests) {

			List<Photo> photos = photoRepository.findAllByAddressContaining(interest.getArea());
			List<Story> stories = new ArrayList<Story>();

			PriorityQueue<Story> pq = new PriorityQueue<Story>(new Comparator<Story>() {

				@Override
				public int compare(Story o1, Story o2) {
					if (o1.getLoveCount() >= o2.getLoveCount()) {
						return 1;
					} else {
						return -1;
					}
				}
			});

			for (Photo photo : photos) {
				pq.add(photo.getStory());
			}

			if (pq.size() < 10) {
				for (int i = 0; i < pq.size(); i++) {
					stories.add(pq.poll());
				}
			} else {
				for (int i = 0; i < 10; i++) {
					stories.add(pq.poll());
				}
			}

			List<StoryResponse> responses = new ArrayList<StoryResponse>();
			
			for (Story story : stories) {
				StoryResponse storyResponse = new StoryResponse();
				
				storyResponse.setStoryNo(story.getStoryNo());
				User storyUser = userRepository.findByUserno(story.getUserNo());
				storyResponse.setNickname(storyUser.getNickname());
				storyResponse.setTitle(story.getTitle());
				storyResponse.setThumbnail(story.getThumbnail());
				storyResponse.setStartDate(story.getStartDate());
				storyResponse.setCreatedDate(story.getCreatedDate());
				storyResponse.setDuration(story.getDuration());
				storyResponse.setStatus(story.getStatus());
				
				responses.add(storyResponse);
			}
			
			storyResponses.add(responses);

		}

		return storyResponses;
	}

	@Override
	public List<StoryResponse> getPopularStory() {
		
		
		List<Story> stories = storyRepository.findTop10ByOrderByLoveCountDesc();
		List<StoryResponse> responses = new ArrayList<StoryResponse>();
		for(Story story : stories) {
			StoryResponse storyResponse = new StoryResponse();
			
			storyResponse.setStoryNo(story.getStoryNo());
			User storyUser = userRepository.findByUserno(story.getUserNo());
			storyResponse.setNickname(storyUser.getNickname());
			storyResponse.setTitle(story.getTitle());
			storyResponse.setThumbnail(story.getThumbnail());
			storyResponse.setStartDate(story.getStartDate());
			storyResponse.setCreatedDate(story.getCreatedDate());
			storyResponse.setDuration(story.getDuration());
			storyResponse.setStatus(story.getStatus());
			
			responses.add(storyResponse);
		}
		return responses;
	}

}
