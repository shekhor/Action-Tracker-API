package com.tigerit.soa.serviceImpl;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.entity.es.MilestoneEntity;
import com.tigerit.soa.entity.es.ProjectEntity;
import com.tigerit.soa.entity.es.TaskEntity;
import com.tigerit.soa.entity.es.TaskHistoryEntity;
import com.tigerit.soa.loginsecurity.repository.UserRepository;
import com.tigerit.soa.repository.es.MilestoneRepository;
import com.tigerit.soa.repository.es.ProjectRepository;
import com.tigerit.soa.repository.es.TaskHistoryRepository;
import com.tigerit.soa.repository.es.TaskRepository;
import com.tigerit.soa.request.task.TaskRequest;
import com.tigerit.soa.request.task.TaskSearchRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.response.task.TaskResponse;
import com.tigerit.soa.service.TaskService;
import com.tigerit.soa.util.*;
import io.netty.util.internal.StringUtil;
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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.*;

/*
Fahim created at 5/31/2020
*/
@Service
@Log4j2
public class TaskServiceImpl implements TaskService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    MilestoneRepository milestoneRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskHistoryRepository taskHistoryRepository;

    @Autowired
    UserRepository userRepository;

    @Qualifier("elasticsearchTemplate")
    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    ValidatorService validatorService;

    @Override
    public ServiceResponse createTask(TaskRequest request, String username) {
        try {
            List<ErrorModel> errorList = validateRequest(request);

            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
            if (userEntityOptional.isPresent()) {
                List<ErrorModel> partOfTeamError = validatorService.isPartOfTeam(request.getProjectId(), userEntityOptional.get());
                if (partOfTeamError.size() > 0) errorList.addAll(partOfTeamError);
            } else {
                log.error("User not found");
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("user.not.found", null, Locale.getDefault()),
                        "userId", "User not fond");

                errorList.add(errorModel);
            }

            UserEntity userEntity = userRepository.findByIdAndUsername(request.getOwnerId(),
                    request.getOwnerName());
            List<ErrorModel> partOfTeamError = validatorService.isPartOfTeam(request.getProjectId(), userEntity);
            if (partOfTeamError.size() > 0) errorList.addAll(partOfTeamError);

            MilestoneEntity milestoneEntity = null;
            if (!StringUtil.isNullOrEmpty(request.getMilestoneId())) {
                milestoneEntity = milestoneRepository.findByIdAndProjectId(request.getMilestoneId(),
                        request.getProjectId());

                if (Objects.isNull(milestoneEntity)) {
                    log.error("Milestone not found for Project " + request.getProjectId());
                    ErrorModel errorModel = new ErrorModel(
                            messageSource.getMessage("milestone.not.found", null, Locale.getDefault()),
                            "milestoneId", "Milestone not fond");

                    errorList.add(errorModel);
                }
            }

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            TaskEntity taskEntity = new TaskEntity();
            String id = String.valueOf(redisUtil.getNextId(RedisKey.TASK_KEY, RedisKey.TASK_INITIAL_VALUE));
            Util.copyProperty(request, taskEntity);
            taskEntity.setId(id);
            taskEntity.setUserId(userEntity.getId());
            taskEntity.setUserName(userEntity.getUsername());
            taskEntity.setOrganizationId(String.valueOf(userEntity.getOrganizationId()));
            taskEntity.setTimeAndUser(username);

            if (!StringUtil.isNullOrEmpty(request.getMilestoneId())) {
                taskEntity.setMilestoneId(milestoneEntity.getId());
                taskEntity.setMilestoneDescription(milestoneEntity.getDescription());
                taskEntity.setMilestoneDate(milestoneEntity.getMilestoneDate());
            }

            taskRepository.indexWithoutRefresh(taskEntity);

            updateTaskHistory(taskEntity);

            TaskResponse response = new TaskResponse();
            Util.copyProperty(taskEntity, response);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
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

    @Override
    public ServiceResponse updateTask(TaskRequest request, String username) {
        try {
            List<ErrorModel> errorList = validateRequest(request);

            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

            Optional<TaskEntity> taskEntityOptional = taskRepository.findByIdAndProjectId(request.getId(),
                    request.getProjectId());

            List<ErrorModel> changeRequestError = validateChangeRequest(taskEntityOptional, userEntityOptional.get());
            if (changeRequestError.size() > 0) errorList.addAll(changeRequestError);

            UserEntity userEntity = null;
            if (!(StringUtil.isNullOrEmpty(request.getOwnerName())) && request.getOwnerId() > 0) {
                if (request.getOwnerId() != taskEntityOptional.get().getOwnerId()) {
                    userEntity = userRepository.findByIdAndUsername(request.getOwnerId(),
                            request.getOwnerName());
                    List<ErrorModel> partOfTeamError = validatorService.isPartOfTeam(request.getProjectId(), userEntity);
                    if (partOfTeamError.size() > 0) errorList.addAll(partOfTeamError);
                }
            }

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            TaskEntity taskEntity = taskEntityOptional.get();

            MilestoneEntity milestoneEntity = null;
            if (StringUtil.isNullOrEmpty(request.getMilestoneId())) { //if we want to remove the milestone
                taskEntity.setMilestoneId(null);
                taskEntity.setMilestoneDescription(null);
                taskEntity.setMilestoneDate(null);
            } else if (!(request.getMilestoneId().equalsIgnoreCase(taskEntity.getMilestoneId()))) { //if we change the milestone
                milestoneEntity = milestoneRepository.findByIdAndProjectId(request.getMilestoneId(),
                        request.getProjectId());

                if (Objects.isNull(milestoneEntity)) {
                    log.error("Milestone not found for Project " + request.getProjectId());
                    ErrorModel errorModel = new ErrorModel(
                            messageSource.getMessage("milestone.not.found", null, Locale.getDefault()),
                            "milestoneId", "Milestone not fond");

                    errorList.add(errorModel);
                } else {
                    taskEntity.setMilestoneId(milestoneEntity.getId());
                    taskEntity.setMilestoneDescription(milestoneEntity.getDescription());
                    taskEntity.setMilestoneDate(milestoneEntity.getMilestoneDate());
                }
            }

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            Util.copyProperty(request, taskEntity);
            taskEntity.setTimeAndUser(username);
            taskEntity.setVersionId(taskEntity.getVersionId() + 1);
            if (Objects.nonNull(userEntity)) {
                taskEntity.setUserId(userEntity.getId());
                taskEntity.setUserName(userEntity.getUsername());
                taskEntity.setOrganizationId(String.valueOf(userEntity.getOrganizationId()));
            }

            taskRepository.indexWithoutRefresh(taskEntity);

            updateTaskHistory(taskEntity);

            TaskResponse response = new TaskResponse();
            Util.copyProperty(taskEntity, response);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
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

    @Override
    public ServiceResponse deleteTask(String taskId, String projectId, String username) {
        try {
            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
            Optional<TaskEntity> taskEntityOptional = taskRepository.findByIdAndProjectId(taskId, projectId);

            List<ErrorModel> errorList = validateChangeRequest(taskEntityOptional, userEntityOptional.get());

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            TaskEntity taskEntity = taskEntityOptional.get();

            taskEntity.setStatus(Status.ARCHIVED.name());
            taskEntity.setTimeAndUser(username);
            taskEntity.setVersionId(taskEntity.getVersionId() + 1);

            taskRepository.indexWithoutRefresh(taskEntity);

            updateTaskHistory(taskEntity);

            TaskResponse response = new TaskResponse();
            Util.copyProperty(taskEntity, response);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
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

    @Override
    public ServiceResponse detailTask(String taskId, String projectId, String username) {
        try {
            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

            List<ErrorModel> errorList = validatorService.isPartOfTeam(projectId, userEntityOptional.get());

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            Optional<TaskEntity> taskEntityOptional = taskRepository.findByIdAndProjectId(taskId, projectId);

            if (!taskEntityOptional.isPresent()) {
                log.error("Task not found");
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("task.not.found", null, Locale.getDefault()),
                        "id", "Task not fond");

                errorList.add(errorModel);
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            TaskEntity taskEntity = taskEntityOptional.get();

            UserEntity userEntity = userEntityOptional.get();

            if (taskEntity.getAccessLevel().equalsIgnoreCase(AccessLevel.PRIVATE.name())) {
                if (taskEntity.getUserId() != userEntity.getId() &&
                        taskEntity.getOwnerId() != userEntity.getId()) {
                    log.error(userEntity.getFirstName() + " is not allowed to perform this task");
                    ErrorModel errorModel = new ErrorModel(
                            "You are not allowed to perform this task",
                            "taskCrate", "Authorization failed");
                    errorList.add(errorModel);
                    return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
                }
            }

            if (taskEntity.getAccessLevel().equalsIgnoreCase(AccessLevel.INTER_COMPANY.name())) {
                if (!(taskEntity.getOrganizationId().equalsIgnoreCase(String.valueOf(userEntity.getOrganizationId())))) {
                    log.error(userEntity.getFirstName() + " is not allowed to perform this task");
                    ErrorModel errorModel = new ErrorModel(
                            "You are not allowed to perform this task",
                            "taskCrate", "Authorization failed");
                    errorList.add(errorModel);
                    return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
                }
            }

            TaskResponse response = new TaskResponse();
            Util.copyProperty(taskEntityOptional.get(), response);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
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

    @Override
    public ServiceResponseExtended actionList(TaskSearchRequest request, String username) {
        try {
            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

            List<ErrorModel> errorList = validatorService.isPartOfTeam(request.getProjectId(), userEntityOptional.get());

            if (errorList.size() > 0) {
                return new ServiceResponseExtended().buildFailedServiceResponseExtended(errorList);
            }

            NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();

            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

            queryBuilder.mustNot(new QueryStringQueryBuilder(Status.ARCHIVED.name()).field("status"));
            queryBuilder.must(new QueryStringQueryBuilder(request.getProjectId()).field("projectId"));
            queryBuilder.must(QueryBuilders.termQuery("userId", userEntityOptional.get().getId()));

            FieldSortBuilder sort = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
            nsqb.withSort(sort);

            PageRequest page = PageRequest.of(request.getPageNo(), request.getTotalItemPerPage());
            nsqb.withPageable(page);

            SearchQuery searchQuery = nsqb.withQuery(queryBuilder).build();

            Page<TaskEntity> taskPage = elasticsearchTemplate.queryForPage(searchQuery, TaskEntity.class);

            List<TaskResponse> taskList = Util.toDtoList(taskPage.getContent(), TaskResponse.class);

            return new ServiceResponseExtended().buildSuccessServiceResponseExtended(taskList, request.getPageNo(),
                    request.getTotalItemPerPage(), taskPage.getTotalElements());
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponseExtended().buildFailedServiceResponseExtended(errorList);
        }
    }

    @Override
    public ServiceResponseExtended searchTask(@Valid TaskSearchRequest request, String username) {
        try {
            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

            List<ErrorModel> errorList = validatorService.isPartOfTeam(request.getProjectId(), userEntityOptional.get());

            if (errorList.size() > 0) {
                return new ServiceResponseExtended().buildFailedServiceResponseExtended(errorList);
            }

            NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();

            BoolQueryBuilder queryBuilder = buildTaskSearchQuery(userEntityOptional.get(), request);

            FieldSortBuilder sort = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
            nsqb.withSort(sort);

            PageRequest page = PageRequest.of(request.getPageNo(), request.getTotalItemPerPage());
            nsqb.withPageable(page);

            SearchQuery searchQuery = nsqb.withQuery(queryBuilder).build();

            Page<TaskEntity> taskPage = elasticsearchTemplate.queryForPage(searchQuery, TaskEntity.class);

            List<TaskResponse> taskList = Util.toDtoList(taskPage.getContent(), TaskResponse.class);

            return new ServiceResponseExtended().buildSuccessServiceResponseExtended(taskList, request.getPageNo(),
                    request.getTotalItemPerPage(), taskPage.getTotalElements());
        } catch (InvalidDataAccessApiUsageException i) {
            log.error("Internal server error " + i.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage(i.getMessage(), null, Locale.getDefault()),
                    "create", i.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponseExtended().buildFailedServiceResponseExtended(errorList);
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponseExtended().buildFailedServiceResponseExtended(errorList);
        }
    }

    private BoolQueryBuilder buildTaskSearchQuery(UserEntity userEntity, TaskSearchRequest request) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(new QueryStringQueryBuilder(request.getProjectId()).field("projectId"));

        if(StringUtil.isNullOrEmpty(request.getAccessLevel()) && request.getOwnerId()<=0) {
            queryBuilder.must(new QueryStringQueryBuilder(AccessLevel.PUBLIC.name()).field("accessLevel"));
        }

        if (!StringUtil.isNullOrEmpty(request.getAccessLevel())) {

            if (request.getAccessLevel().equalsIgnoreCase(AccessLevel.PRIVATE.name())) {
                queryBuilder.must(QueryBuilders.termQuery("ownerId", userEntity.getId()));
                queryBuilder.must(new QueryStringQueryBuilder(AccessLevel.PRIVATE.name()).field("accessLevel"));
            }

            if (request.getAccessLevel().equalsIgnoreCase(AccessLevel.PUBLIC.name())) {
                queryBuilder.must(new QueryStringQueryBuilder(AccessLevel.PUBLIC.name()).field("accessLevel"));
            }

            if (request.getAccessLevel().equalsIgnoreCase(AccessLevel.INTER_COMPANY.name())) {
                queryBuilder.must(QueryBuilders.termQuery("organizationId", userEntity.getOrganizationId()));
                queryBuilder.must(new QueryStringQueryBuilder(AccessLevel.INTER_COMPANY.name()).field("accessLevel"));
            }
        }

        if (Objects.nonNull(request.getOwnerId()) && request.getOwnerId() > 0) {

            if (request.getOwnerId() == userEntity.getId()) {
                queryBuilder.must(QueryBuilders.termQuery("ownerId", userEntity.getId()));
            } else {
                if(StringUtil.isNullOrEmpty(request.getAccessLevel())) {
                    BoolQueryBuilder publicOrOwnerAccess = QueryBuilders.boolQuery();
                    publicOrOwnerAccess.should(QueryBuilders.termQuery("ownerId", request.getOwnerId()));
                    publicOrOwnerAccess.should(new QueryStringQueryBuilder(AccessLevel.PUBLIC.name()).field("accessLevel"));
                    publicOrOwnerAccess.minimumShouldMatch(2);

                    queryBuilder.must(publicOrOwnerAccess);

//                    BoolQueryBuilder interCompanyAccess = QueryBuilders.boolQuery();
//                    interCompanyAccess.should(QueryBuilders.termQuery("ownerId", request.getOwnerId()));
//                    interCompanyAccess.should(QueryBuilders.termQuery("organizationId", userEntity.getOrganizationId()));
//                    interCompanyAccess.should(new QueryStringQueryBuilder(AccessLevel.INTER_COMPANY.name()).field("accessLevel"));
//                    interCompanyAccess.minimumShouldMatch(2);
//
//                    queryBuilder.should(interCompanyAccess);
                } else {
                    if(request.getAccessLevel().equalsIgnoreCase(AccessLevel.INTER_COMPANY.name())) {
                        queryBuilder.must(QueryBuilders.termQuery("ownerId", request.getOwnerId()));
                        queryBuilder.must(QueryBuilders.termQuery("organizationId", userEntity.getOrganizationId()));
                        queryBuilder.must(new QueryStringQueryBuilder(AccessLevel.INTER_COMPANY.name()).field("accessLevel"));
                    } else if(request.getAccessLevel().equalsIgnoreCase(AccessLevel.PUBLIC.name())){
                        queryBuilder.must(QueryBuilders.termQuery("ownerId", request.getOwnerId()));
                        queryBuilder.must(new QueryStringQueryBuilder(AccessLevel.PUBLIC.name()).field("accessLevel"));
                    } else {
                        throw new InvalidDataAccessApiUsageException("You can not access other user private task");
                    }
                }
            }
        }

        if (!StringUtil.isNullOrEmpty(request.getId())) {
            queryBuilder.must(new QueryStringQueryBuilder(request.getId()).field("id"));
        }

        if (!StringUtil.isNullOrEmpty(request.getTaskName())) {
            queryBuilder.must(new QueryStringQueryBuilder(request.getTaskName()).field("taskName"));
        }

        if (!StringUtil.isNullOrEmpty(request.getParentTaskId())) {
            queryBuilder.must(new QueryStringQueryBuilder(request.getParentTaskId()).field("parentTaskId"));
        }

        if (!StringUtil.isNullOrEmpty(request.getDepartmentId())) {
            queryBuilder.must(new QueryStringQueryBuilder(request.getDepartmentId()).field("departmentId"));
        }

        if (!StringUtil.isNullOrEmpty(request.getStatus())) {
            queryBuilder.must(new QueryStringQueryBuilder(request.getStatus()).field("status"));
        }

        if (!StringUtil.isNullOrEmpty(request.getCategory())) {
            queryBuilder.must(new QueryStringQueryBuilder(request.getCategory()).field("category"));
        }

        if (!StringUtil.isNullOrEmpty(request.getMilestoneId())) {
            queryBuilder.must(new QueryStringQueryBuilder(request.getMilestoneId()).field("milestoneId"));
        }

        if (Objects.nonNull(request.getStartDate())) {
            queryBuilder.must(QueryBuilders.rangeQuery("startDate")
                    .from(Util.getStartOfDay(request.getStartDate()))
                    .to(Util.getEndOfDay(request.getStartDate())));
        }

        if (Objects.nonNull(request.getEndDate())) {
            queryBuilder.must(QueryBuilders.rangeQuery("endDate")
                    .from(Util.getStartOfDay(request.getEndDate()))
                    .to(Util.getEndOfDay(request.getEndDate())));
        }

        if (Objects.nonNull(request.getDueDate())) {
            queryBuilder.must(QueryBuilders.rangeQuery("dueDate")
                    .from(Util.getStartOfDay(request.getDueDate()))
                    .to(Util.getEndOfDay(request.getDueDate())));
        }

        return queryBuilder;
    }

    private List<ErrorModel> validateRequest(TaskRequest request) {
        List<ErrorModel> errorList = new ArrayList<>();

        Integer count = projectRepository.countByIdAndProjectNameAndStatus(request.getProjectId(),
                request.getProjectName(), Status.ACTIVE.name());

        if (count <= 0) {
            log.error("Project not found for " + request.getProjectId());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("project.not.found", null, Locale.getDefault()),
                    "projectId", "Project not fond");

            errorList.add(errorModel);
        }

        return errorList;
    }

    private List<ErrorModel> validateChangeRequest(Optional<TaskEntity> taskEntityOptional, UserEntity userEntity) {
        List<ErrorModel> errorList = new ArrayList<>();

        if (Objects.isNull(userEntity)) {
            log.error("User not found");
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("user.not.found", null, Locale.getDefault()),
                    "userId", "User not fond");

            errorList.add(errorModel);

            return errorList;
        }

        if (!taskEntityOptional.isPresent()) {
            log.error("Task not found");
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("task.not.found", null, Locale.getDefault()),
                    "id", "Task not fond");

            errorList.add(errorModel);
            return errorList;
        }

        TaskEntity taskEntity = taskEntityOptional.get();

        //check if the user is the assignee/owner of the task
        if (taskEntity.getUserId() == userEntity.getId() ||
                taskEntity.getOwnerId() == userEntity.getId()) {
            return errorList;
        }

        NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(new QueryStringQueryBuilder(taskEntity.getProjectId()).field("id"));
        queryBuilder.must(new QueryStringQueryBuilder(Status.ACTIVE.name()).field("status"));
        queryBuilder.must(QueryBuilders.multiMatchQuery(String.valueOf(userEntity.getId()),
                "projectOwner", "projectManager"));

        SearchQuery searchQuery = nsqb.withQuery(queryBuilder).build();

        long result = elasticsearchTemplate.count(searchQuery, ProjectEntity.class);

        if (result <= 0) {
            log.error(userEntity.getFirstName() + " is not allowed to perform this task");
            ErrorModel errorModel = new ErrorModel(
                    "You are not allowed to perform this task",
                    "taskCrate", "Authorization failed");
            errorList.add(errorModel);
        }

        return errorList;
    }

    private void updateTaskHistory(TaskEntity taskEntity) {
        TaskHistoryEntity taskHistoryEntity = new TaskHistoryEntity();

        String id = String.valueOf(redisUtil.getNextId(RedisKey.TASK_HISTORY_KEY,
                RedisKey.TASK_HISTORY_INITIAL_VALUE));

        Util.copyProperty(taskEntity, taskHistoryEntity);
        taskHistoryEntity.setId(id);
        taskHistoryEntity.setTaskIndexId(taskEntity.getId());

        taskHistoryRepository.indexWithoutRefresh(taskHistoryEntity);

        log.debug(taskEntity.getTaskName() + " history added");
    }

    @Override
    public void syncProjectHeaderUpdate(String projectId, String projectName) {
        try {
            int pageSize = 1000;
            NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();

            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(new QueryStringQueryBuilder(projectId).field("projectId"));

            SearchQuery searchQuery = nsqb.withQuery(queryBuilder).build();
            long totalTask = elasticsearchTemplate.count(searchQuery, TaskEntity.class);
            log.info("Total task to be updated: " + totalTask);

            long pageCount = totalTask/pageSize;

            Page<TaskEntity> taskPage;
            List<TaskEntity> taskList = new ArrayList<>();
            PageRequest page = null;
            FieldSortBuilder sort = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);

            for(int i=0;i<=pageCount;i++) {
                log.info("page no " +i);
                page = PageRequest.of(i, pageSize);
                nsqb.withPageable(page);
                nsqb.withSort(sort);
                searchQuery = nsqb.withQuery(queryBuilder).build();

                taskPage = elasticsearchTemplate.queryForPage(searchQuery, TaskEntity.class);
                taskList = taskPage.getContent();
                for(TaskEntity taskEntity : taskList) {
                    taskEntity.setProjectName(projectName);
                }
                taskRepository.saveAll(taskList);
                log.info("Total task updated: " + (i*pageSize+taskList.size()));
            }

        } catch (Exception e) {
            log.error("error occurred : " + e.getMessage());
        }
    }
}
