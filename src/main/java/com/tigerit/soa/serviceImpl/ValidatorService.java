package com.tigerit.soa.serviceImpl;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.entity.es.ProjectEntity;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.util.Status;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
Fahim created at 6/8/2020
*/
@Service
@Log4j2
public class ValidatorService {

    @Autowired
    MessageSource messageSource;

    @Qualifier("elasticsearchTemplate")
    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    public List<ErrorModel> isPartOfTeam(String projectId, UserEntity userEntity) {
        List<ErrorModel> errorList = new ArrayList<>();

        if (Objects.isNull(userEntity)) {
            log.error("User not found");
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("user.not.found", null, Locale.getDefault()),
                    "userId", "User not fond");

            errorList.add(errorModel);

            return errorList;
        }

        NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(new QueryStringQueryBuilder(projectId).field("id"));
        queryBuilder.must(new QueryStringQueryBuilder(Status.ACTIVE.name()).field("status"));
        queryBuilder.must(QueryBuilders.multiMatchQuery(String.valueOf(userEntity.getId()),
                "projectOwner", "projectManager", "teamMemberList.userId"));

        SearchQuery searchQuery = nsqb.withQuery(queryBuilder).build();

        long result = elasticsearchTemplate.count(searchQuery, ProjectEntity.class);

        if (result <= 0) {
            log.error(userEntity.getFirstName() + " is not allowed to perform this task");
            ErrorModel errorModel = new ErrorModel(
                    "You are not allowed to perform this task",
                    "notPartOfTeam", "Authorization failed");
            errorList.add(errorModel);
        }

        return errorList;
    }
}
