package com.tigerit.soa.loginsecurity.repository;

import com.tigerit.soa.entity.activity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityUserRepository extends JpaRepository<UserActivity, Long> {
}

