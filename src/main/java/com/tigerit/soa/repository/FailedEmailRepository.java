package com.tigerit.soa.repository;

import com.tigerit.soa.entity.FailedEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by DIPU on 6/4/20
 */
public interface FailedEmailRepository  extends JpaRepository<FailedEmailEntity, Long> {
}
