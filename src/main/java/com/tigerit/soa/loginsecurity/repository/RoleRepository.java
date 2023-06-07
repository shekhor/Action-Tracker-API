package com.tigerit.soa.loginsecurity.repository;

import com.tigerit.soa.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
