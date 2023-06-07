package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.TaskHistoryEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TaskHistoryRepository extends ElasticsearchRepository<TaskHistoryEntity, String> {
}
