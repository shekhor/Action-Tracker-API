package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.CategoryHistoryEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryHistoryRepository extends ElasticsearchRepository<CategoryHistoryEntity, String> {
}
