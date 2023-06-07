package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends ElasticsearchRepository<OrganizationEntity, String> {

    OrganizationEntity findByIdAndDomainName(String id, String domainName);

    OrganizationEntity findByIdAndStatus(String id, String status);

    Integer countByDomainNameAndStatus(String domainName, String status);

    OrganizationEntity findByDomainNameAndStatus(String domainName, String status);

    Page<OrganizationEntity> findAllByTypeAndStatus(String type, String status, Pageable pageable);
}
