//package com.tigerit.soa.loginsecurity.security.services;
//
//import com.tigerit.soa.loginsecurity.entity.User;
//import com.tigerit.soa.entity.UserEntity;
//import com.tigerit.soa.loginsecurity.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//	@Autowired
//	UserRepository userRepository;
//
//	@Override
//	@Transactional
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		UserEntity user = userRepository.findByUsername(username)
//				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
//
//		return UserDetailsImpl.build(user);
//	}
//	//this was for User test
//	/*@Override
//	@Transactional
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		User user = userRepository.findByUsername(username)
//				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
//
//		return UserDetailsImpl.build(user);
//	}*/
//
//}
