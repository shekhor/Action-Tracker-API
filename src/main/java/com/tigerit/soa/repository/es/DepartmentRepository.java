package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Created by DIPU on 4/8/20
 */

@Service
public interface DepartmentRepository extends ElasticsearchRepository<DepartmentEntity, String> {

    Optional<DepartmentEntity> findByDepartmentNameAndOrganizationId(String departmentName, String orgId);
    DepartmentEntity findByIdAndOrganizationId(String id, String orgId);

    Integer countByOrganizationId(String organizationId);
    Page<DepartmentEntity> findByOrganizationId(String orgId, Pageable page);
}
