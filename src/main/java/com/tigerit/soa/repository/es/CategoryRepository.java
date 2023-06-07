package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.CategoryEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ElasticsearchRepository<CategoryEntity, String> {

    Integer countByProjectIdAndCategoryNameAndStatus(String projectId, String categoryName, String status);

    CategoryEntity findByIdAndProjectIdAndStatus(String id, String projectId, String status);
}
