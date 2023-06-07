package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.TeamMemberHistoryEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberHistoryRepository extends ElasticsearchRepository<TeamMemberHistoryEntity, String> {

}
