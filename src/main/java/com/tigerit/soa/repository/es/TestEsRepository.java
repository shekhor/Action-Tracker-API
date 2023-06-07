package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.TestEsEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TestEsRepository extends ElasticsearchRepository<TestEsEntity, String> {

}
