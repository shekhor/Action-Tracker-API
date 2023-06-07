package com.tigerit.soa.serviceImpl;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.entity.es.CategoryEntity;
import com.tigerit.soa.entity.es.CategoryHistoryEntity;
import com.tigerit.soa.entity.es.ProjectEntity;
import com.tigerit.soa.loginsecurity.repository.UserRepository;
import com.tigerit.soa.model.es.Category;
import com.tigerit.soa.repository.es.CategoryHistoryRepository;
import com.tigerit.soa.repository.es.CategoryRepository;
import com.tigerit.soa.repository.es.ProjectRepository;
import com.tigerit.soa.request.category.CategoryListRequest;
import com.tigerit.soa.request.category.CategoryMappingRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.CategoryService;
import com.tigerit.soa.util.*;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

/*
Fahim created at 5/14/2020
*/
@Log4j2
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryHistoryRepository categoryHistoryRepository;

    @Qualifier("elasticsearchTemplate")
    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    UserRepository userRepository;

    @Override
    public ServiceResponse mapping(CategoryMappingRequest request, String username) {
        try {
            List<ErrorModel> errorList = validateCategoryMapping(request, username);

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            for (Category category : request.getCategoryList()) {
                ErrorModel errorModel = null;
                if (Operation.CREATE.name().equalsIgnoreCase(category.getOperation())) {
                    errorModel = createCategory(category, request, username);
                } else if (Operation.UPDATE.name().equalsIgnoreCase(category.getOperation())) {
                    errorModel = updateCategory(category, request, username);
                } else if (Operation.DELETE.name().equalsIgnoreCase(category.getOperation())) {
                    errorModel = deleteCategory(category, request, username);
                }
                if (errorModel != null) {
                    errorList.add(errorModel);
                }
            }
            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, true, Collections.emptyList());
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
        }
    }

    private List<ErrorModel> validateCategoryMapping(CategoryMappingRequest request, String userName) {
        List<ErrorModel> errorList = new ArrayList<>();

        ProjectEntity projectEntity = projectRepository.findByIdAndStatus(
                request.getProjectId(), Status.ACTIVE.name());

        if (Objects.isNull(projectEntity)) {
            log.error("Project not found for " + request.getProjectId());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("project.not.found", null, Locale.getDefault()),
                    "projectId", "Project not fond");

            errorList.add(errorModel);
            return errorList;
        }

        UserEntity userEntity = userRepository.findByUsername(userName).get();
        boolean bool = Util.isEqual(userEntity.getId(), projectEntity.getProjectManager());
        boolean bool2 = Util.isEqual(userEntity.getId(), projectEntity.getProjectOwner());
        if (!bool && !bool2) {
            log.error("User must be Project Owner or Project Manager to perform this task");
            ErrorModel errorModel = new ErrorModel(
                    "User must be Project Owner or Project Manager to perform this task",
                    "project", "Authorization failed");
            errorList.add(errorModel);
            return errorList;
        }

        return errorList;
    }

    private ErrorModel createCategory(Category category, CategoryMappingRequest request, String username) {
        try {
            if (categoryRepository.countByProjectIdAndCategoryNameAndStatus(request.getProjectId(),
                    category.getCategoryName(), Status.ACTIVE.name()) > 0) {
                log.error(category.getCategoryName() + " is already added");
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage(category.getCategoryName() + " is already added",
                                null, Locale.getDefault()),
                        "category", "");
                return errorModel;
            }

            CategoryEntity categoryEntity = new CategoryEntity();
            String id = String.valueOf(redisUtil.getNextId(RedisKey.CATEGORY_KEY, RedisKey.CATEGORY_INITIAL_VALUE));
            categoryEntity.setId(id);
            categoryEntity.setProjectId(request.getProjectId());
            categoryEntity.setCategoryName(category.getCategoryName());
            categoryEntity.setStatus(Status.ACTIVE.name());
            categoryEntity.setVersionId(1l);
            categoryEntity.setTimeAndUser(username);

            categoryRepository.save(categoryEntity);

            updateCategoryHistory(categoryEntity);

            log.debug(categoryEntity.getCategoryName() + " Mapped to project");
        } catch (Exception e) {
            log.error("Error occurred adding category " + category.getCategoryName()
                    + " Error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("Error occurred adding category "
                            + category.getCategoryName(), null, Locale.getDefault()),
                    "category", e.getMessage());
            return errorModel;
        }
        return null;
    }

    private ErrorModel updateCategory(Category category, CategoryMappingRequest request, String username) {
        try {
            CategoryEntity categoryEntity = categoryRepository.findByIdAndProjectIdAndStatus(
                    category.getId(), request.getProjectId(), Status.ACTIVE.name());

            if (Objects.isNull(categoryEntity)) {
                log.error("No category found for category " + category.getCategoryName());
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("No category found for category "
                                + category.getCategoryName(), null, Locale.getDefault()),
                        "category", "");
                return errorModel;
            }

            if(categoryRepository.countByProjectIdAndCategoryNameAndStatus(request.getProjectId(),
                    category.getCategoryName(), Status.ACTIVE.name())>0) {
                log.error("Duplicate category found for category " + category.getCategoryName());
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("Duplicate category found for category "
                                + category.getCategoryName(), null, Locale.getDefault()),
                        "category", "");
                return errorModel;
            }

            categoryEntity.setCategoryName(category.getCategoryName());
            categoryEntity.setVersionId(categoryEntity.getVersionId() + 1);
            categoryEntity.setTimeAndUser(username);

            categoryRepository.save(categoryEntity);

            updateCategoryHistory(categoryEntity);

            log.debug(categoryEntity.getCategoryName() + " updated");
        } catch (Exception e) {
            log.error("Error occurred updating category " + category.getCategoryName()
                    + " Error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("Error occurred updating category "
                            + category.getCategoryName(), null, Locale.getDefault()),
                    "category", e.getMessage());
            return errorModel;
        }
        return null;
    }

    private ErrorModel deleteCategory(Category category, CategoryMappingRequest request, String username) {
        try {
            CategoryEntity categoryEntity = categoryRepository.findByIdAndProjectIdAndStatus(
                    category.getId(), request.getProjectId(), Status.ACTIVE.name());

            if (Objects.isNull(categoryEntity)) {
                log.error("No category found for category " + category.getCategoryName());
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("No category found for category "
                                + category.getCategoryName(), null, Locale.getDefault()),
                        "category", "");
                return errorModel;
            }
            categoryEntity.setStatus(Status.DELETED.name());
            categoryEntity.setVersionId(categoryEntity.getVersionId() + 1);
            categoryEntity.setTimeAndUser(username);

            categoryRepository.save(categoryEntity);

            updateCategoryHistory(categoryEntity);

            log.debug(categoryEntity.getCategoryName() + " deleted");
        } catch (Exception e) {
            log.error("Error occurred deleting category " + category.getCategoryName()
                    + " Error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("Error occurred deleting category "
                            + category.getCategoryName(), null, Locale.getDefault()),
                    "category", e.getMessage());
            return errorModel;
        }
        return null;
    }

    private void updateCategoryHistory(CategoryEntity categoryEntity) {
        CategoryHistoryEntity categoryHistoryEntity = new CategoryHistoryEntity();

        String id = String.valueOf(redisUtil.getNextId(RedisKey.CATEGORY_HISTORY_KEY,
                RedisKey.CATEGORY_HISTORY_INITIAL_VALUE));

        Util.copyProperty(categoryEntity, categoryHistoryEntity);
        categoryHistoryEntity.setId(id);
        categoryHistoryEntity.setCategoryIndexId(categoryEntity.getId());

        categoryHistoryRepository.save(categoryHistoryEntity);

        log.debug(categoryEntity.getCategoryName() + " history added");
    }

    @Override
    public ServiceResponse list(CategoryListRequest request, String username) {
        try {
            NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();

            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

            queryBuilder.must(new QueryStringQueryBuilder(Status.ACTIVE.name()).field("status"));
            queryBuilder.must(new QueryStringQueryBuilder(request.getProjectId()).field("projectId"));

            FieldSortBuilder sort = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
            nsqb.withSort(sort);

            //PageRequest page = PageRequest.of(request.getPageNo(), request.getTotalItemPerPage());
            //nsqb.withPageable(page);

            SearchQuery searchQuery = nsqb.withQuery(queryBuilder)
                    .withFields("id", "projectId", "categoryName")
                    .build();

            List<CategoryEntity> categoryEntityList = elasticsearchTemplate.queryForList(searchQuery, CategoryEntity.class);

            log.debug("Cateogry list size " + categoryEntityList.size());
            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, categoryEntityList, Collections.emptyList());
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
        }
    }
}
