package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.TestUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by DIPU on 4/20/20
 */
public interface TestUserRepository extends ElasticsearchRepository<TestUser, Long> {
}
