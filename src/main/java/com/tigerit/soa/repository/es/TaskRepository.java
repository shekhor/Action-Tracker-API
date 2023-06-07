package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.TaskEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface TaskRepository extends ElasticsearchRepository<TaskEntity, String> {

    Optional<TaskEntity> findByIdAndProjectId(String id, String projectId);
}
