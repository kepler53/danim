package com.pd.danim.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.pd.danim.DTO.Comment;
import com.pd.danim.DTO.Story;

public interface CommentRepository extends CrudRepository<Comment, Long> {
	Comment findByCommentNo(long commentNo);
	List<Comment> findAllByStoryAndDeleted(Story story, boolean isDeleted);
	void deleteAllByStory(Story story);
	
}
