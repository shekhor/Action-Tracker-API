package com.tigerit.soa.loginsecurity.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 *
 */
public class NoOpAuthManager implements AuthenticationManager {

    private Logger logger = LoggerFactory.getLogger(NoOpAuthManager.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("No Operation Authentication manager is invoked");
        return authentication;
    }

}
