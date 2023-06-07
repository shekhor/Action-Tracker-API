package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.MilestoneEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MilestoneRepository extends ElasticsearchRepository<MilestoneEntity, String> {

    MilestoneEntity findByIdAndProjectId(String id, String projectId);
}
