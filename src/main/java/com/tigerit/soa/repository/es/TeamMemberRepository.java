package com.tigerit.soa.repository.es;

import com.tigerit.soa.entity.es.TeamMemberEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends ElasticsearchRepository<TeamMemberEntity, String> {

    Integer countByUserIdAndProjectIdAndStatus(Long userId, String projectId, String status);

    TeamMemberEntity findFirstByIdAndProjectIdAndStatus(String id, String projectId, String status);

    List<TeamMemberEntity> findAllByProjectIdAndStatus(String projectId, String status);
}
