package com.pd.danim.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pd.danim.DTO.DanimId;
import com.pd.danim.DTO.SecurityMember;
import com.pd.danim.DTO.User;
import com.pd.danim.Repository.DanimRepository;
import com.pd.danim.Repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;

@Service
public class MyUserDetailService implements UserDetailsService{
	
	@Autowired
	private DanimRepository DanimRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, ExpiredJwtException {
		
		DanimId danim = DanimRepository.findById(username);
		
		if(danim == null) {
			throw new UsernameNotFoundException(username+ ": 해당되는 사용자는 존재하지 않습니다.");
		}
		
		User user = userRepository.findByUserno(danim.getUserno());
		String role = user.getRole().toString();
		
		return new SecurityMember(danim, role);
	}

}
