package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.OrganizationHistoryEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OrganizationHistoryRepository extends ElasticsearchRepository<OrganizationHistoryEntity, String> {
}
