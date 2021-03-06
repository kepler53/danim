package com.pd.danim.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pd.danim.DTO.Photo;
import com.pd.danim.DTO.Story;
import com.pd.danim.DTO.StoryStatus;
import com.pd.danim.DTO.User;
import com.pd.danim.Form.Response.SearchByAreaResponse;
import com.pd.danim.Form.Response.SearchByNicknameResponse;
import com.pd.danim.Repository.PhotoRepository;
import com.pd.danim.Repository.StoryRepository;
import com.pd.danim.Repository.UserRepository;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private StoryRepository storyRepository;

	@Override
	public List<SearchByNicknameResponse> searchByNickName(String nickname) {

		List<SearchByNicknameResponse> responses = new ArrayList<>();

		List<User> users = userRepository.findByNicknameContaining(nickname);

		for (User user : users) {
			SearchByNicknameResponse response = new SearchByNicknameResponse();
			response.setNickname(user.getNickname());
			response.setProfile(user.getProfile());
			responses.add(response);
		}

		return responses;
	}

	@Override
	public List<SearchByAreaResponse> searchByArea(String area) {

		List<SearchByAreaResponse> responses = new ArrayList<>();

		List<Photo> photos = photoRepository.findTop1000ByAddressContaining(area);


		List<Story> stories = new ArrayList<Story>();

		Set<Long> storySet = new HashSet<Long>();
		for (Photo photo : photos) {
			storySet.add(photo.getStory().getStoryNo());
			if (storySet.size() >= 10) {
				break;
			}
		}

		Iterator<Long> iter = storySet.iterator();
		while (iter.hasNext()) {
			Long variable = iter.next();
			Story story = storyRepository.findByStoryNo(variable);
			stories.add(story);
		}

		for (Story story : stories) {
			if (story.getStatus().equals(StoryStatus.PUBLISHED)) {
				SearchByAreaResponse response = new SearchByAreaResponse();
				response.setPhotoFileName(story.getThumbnail());
				response.setStoryNo(story.getStoryNo());
				response.setTitle(story.getTitle());
				User user = userRepository.findByUserno(story.getUserNo());
				response.setNickname(user.getNickname());
				responses.add(response);
			}
		}

		return responses;
	}

}
