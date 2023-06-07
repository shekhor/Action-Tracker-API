package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.ProjectEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by DIPU on 5/12/20
 */
public interface ProjectRepository extends ElasticsearchRepository<ProjectEntity, String> {

    ProjectEntity findByIdAndStatus(String id, String status);

    Integer countByIdAndStatus(String id, String status);

    Integer countByIdAndProjectNameAndStatus(String id, String projectName, String status);
}
