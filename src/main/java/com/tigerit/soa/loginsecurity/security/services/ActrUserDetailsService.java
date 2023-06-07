package com.tigerit.soa.loginsecurity.security.services;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.loginsecurity.models.ActrUserDetails;
import com.tigerit.soa.loginsecurity.repository.UserRepository;
import com.tigerit.soa.loginsecurity.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Service
public class ActrUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(ActrUserDetailsService.class);


    private UserRepository userRepository;

    public ActrUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Inside loadUserByUsername with username : {}", username);
        Optional<UserEntity> users = userRepository.findByUsername(username);
        List<String> roles = new ArrayList<>();
        if (users.isPresent()) {
            logger.debug("User Found : {}", users.get().toString());
            List<String> methodAccessList =
                    userRepository.findMethodAccessListByUsername(users.get().getUsername());
            roles = Util.methodAccessListToUniqueRoles(methodAccessList);
            logger.debug("Size of Role Array for user : {}, is {}", username, roles.size());
        } else {
            throw new UsernameNotFoundException(username);
        }
        return new ActrUserDetails(users.get().getUsername(), users.get().getEncryptedPassword(), roles);
    }
}
