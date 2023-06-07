package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.TestEsEntity;
import com.tigerit.soa.entity.es.TestEsEntity2;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/*
Fahim created at 4/19/2020
*/
public interface TestEs2Repository extends ElasticsearchRepository<TestEsEntity2, String> {

}
