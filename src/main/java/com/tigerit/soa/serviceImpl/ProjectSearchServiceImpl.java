package com.tigerit.soa.serviceImpl;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.entity.es.ProjectEntity;
import com.tigerit.soa.loginsecurity.repository.UserRepository;
import com.tigerit.soa.request.project.ProjectListRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.service.ProjectSearchService;
import com.tigerit.soa.util.Status;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/*
Fahim created at 5/17/2020
*/
@Service
@Log4j2
public class ProjectSearchServiceImpl implements ProjectSearchService {

    @Autowired
    MessageSource messageSource;

    @Qualifier("elasticsearchTemplate")
    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    UserRepository userRepository;

    @Override
    public ServiceResponseExtended list(ProjectListRequest request, String username) {
        ServiceResponseExtended serviceResponseExtended = new ServiceResponseExtended();
        try {
            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

            if(!userEntityOptional.isPresent()) {
                List<ErrorModel> errorList = new ArrayList<>();
                log.error("User not not found for " + username);
                ErrorModel errorModel = new ErrorModel("NO user found for " + username,
                        "user", "No user found");
                errorList.add(errorModel);
                return serviceResponseExtended.buildFailedServiceResponseExtended(errorList);
            }

            UserEntity userEntity = userEntityOptional.get();

            NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();

            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

            queryBuilder.must(new QueryStringQueryBuilder(Status.ACTIVE.name()).field("status"));
            queryBuilder.must(QueryBuilders.multiMatchQuery(String.valueOf(userEntity.getId()),
                    "projectOwner", "projectManager","teamMemberList.userId"));

            FieldSortBuilder sort = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
            nsqb.withSort(sort);

            PageRequest page = PageRequest.of(request.getPageNo(), request.getTotalItemPerPage());
            nsqb.withPageable(page);

            SearchQuery searchQuery = nsqb.withQuery(queryBuilder)
                    .withFields("id","projectName","description","parentProjectId","projectOwner",
                            "projectOwnerName", "projectManager", "projectManagerName","departmentId")
                    .build();

            Page<ProjectEntity> projectPage = elasticsearchTemplate.queryForPage(searchQuery,
                    ProjectEntity.class);

            log.debug("Project list size " + projectPage.getTotalElements());

            return serviceResponseExtended.buildSuccessServiceResponseExtended(projectPage.getContent(),
                    request.getPageNo(), request.getTotalItemPerPage(), projectPage.getTotalElements());
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "projectList", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return serviceResponseExtended.buildFailedServiceResponseExtended(errorList);
        }
    }
}
