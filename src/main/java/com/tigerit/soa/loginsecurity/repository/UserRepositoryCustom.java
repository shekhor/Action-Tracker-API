package com.tigerit.soa.loginsecurity.repository;

import com.tigerit.soa.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 *
 */
@NoRepositoryBean
public interface UserRepositoryCustom {
    List<String> findMethodAccessListByUsername(String username);

    //Page<UserEntity> findAllUserBySearchParams(String userCategory, Long partnerId, String username, Pageable pageable);
}
