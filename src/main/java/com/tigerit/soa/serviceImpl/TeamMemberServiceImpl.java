package com.tigerit.soa.serviceImpl;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.entity.es.ProjectEntity;
import com.tigerit.soa.entity.es.TeamMemberEntity;
import com.tigerit.soa.entity.es.TeamMemberHistoryEntity;
import com.tigerit.soa.loginsecurity.repository.UserRepository;
import com.tigerit.soa.model.es.TeamMember;
import com.tigerit.soa.repository.es.ProjectRepository;
import com.tigerit.soa.repository.es.TeamMemberHistoryRepository;
import com.tigerit.soa.repository.es.TeamMemberRepository;
import com.tigerit.soa.request.teammember.TeamMemberListRequest;
import com.tigerit.soa.request.teammember.TeamMemberMappingRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.TeamMemberService;
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
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

/*
Fahim created at 5/13/2020
*/
@Log4j2
@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    TeamMemberRepository teamMemberRepository;

    @Autowired
    TeamMemberHistoryRepository teamMemberHistoryRepository;

    @Qualifier("elasticsearchTemplate")
    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    ValidatorService validatorService;

    @Override
    public ServiceResponse mapping(TeamMemberMappingRequest request, String username) {
        try {
            List<ErrorModel> errorList = validateTeamMemberMapping(request, username);

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            errorList = new ArrayList<>();

            for (TeamMember teamMember : request.getTeamMemberList()) {
                ErrorModel errorModel = null;
                if (Operation.CREATE.name().equalsIgnoreCase(teamMember.getOperation())) {
                    errorModel = createTeamMember(teamMember, request, username);
                } else if (Operation.UPDATE.name().equalsIgnoreCase(teamMember.getOperation())) {
                    errorModel = updateTeamMember(teamMember, request, username);
                } else if (Operation.DELETE.name().equalsIgnoreCase(teamMember.getOperation())) {
                    errorModel = deleteTeamMember(teamMember, request, username);
                }
                if (errorModel != null) {
                    errorList.add(errorModel);
                }
            }

            ErrorModel error = updateProject(request, username);
            if (error != null) {
                errorList.add(error);
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

    private ErrorModel updateProject(TeamMemberMappingRequest request, String username) {
        try {
            ProjectEntity projectEntity = projectRepository.findByIdAndStatus(
                    request.getProjectId(), Status.ACTIVE.name());

            if (Objects.isNull(projectEntity)) {
                log.error("Project not found for " + request.getProjectId());
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("project.not.found", null, Locale.getDefault()),
                        "projectId", "Project not fond");

                return errorModel;
            }

            if (Objects.nonNull(request.getProjectOwner())) {
                projectEntity.setProjectOwner(request.getProjectOwner().getUserId());
                projectEntity.setProjectOwnerName(request.getProjectOwner().getFirstName());
            }

            if (Objects.nonNull(request.getProjectManager())) {
                projectEntity.setProjectManager(request.getProjectManager().getUserId());
                projectEntity.setProjectManagerName(request.getProjectManager().getFirstName());
            }
            projectEntity.setEditedBy(username);

            BoolQueryBuilder builder = QueryBuilders.boolQuery();

            //AND Relation
            builder.must(new QueryStringQueryBuilder(request.getProjectId()).field("projectId"));
            builder.must(new QueryStringQueryBuilder(Status.ACTIVE.name()).field("status"));

            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            nativeSearchQueryBuilder.withQuery(builder);

            NativeSearchQuery searchQuery = nativeSearchQueryBuilder.withFields("id", "userId").build();

            List<TeamMemberEntity> teamMemberList = elasticsearchTemplate.queryForList(searchQuery, TeamMemberEntity.class);

            List<TeamMember> projectTeamMemberList = new ArrayList<>();

            for (TeamMemberEntity teamMemberEntity : teamMemberList) {
                TeamMember teamMember = new TeamMember();
                teamMember.setId(teamMemberEntity.getId());
                teamMember.setUserId(teamMemberEntity.getUserId());
                projectTeamMemberList.add(teamMember);
            }
            projectEntity.setTeamMemberList(projectTeamMemberList);

            projectRepository.save(projectEntity);
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            return errorModel;
        }
        return null;
    }


    private ErrorModel createTeamMember(TeamMember teamMember, TeamMemberMappingRequest request,
                                        String username) {
        try {
            if (teamMemberRepository.countByUserIdAndProjectIdAndStatus(teamMember.getUserId(),
                    request.getProjectId(), Status.ACTIVE.name()) > 0) {
                log.error(teamMember.getFirstName() + " Already assigned to the project");
                ErrorModel errorModel = new ErrorModel(
                        teamMember.getFirstName() + " Already assigned to the project",
                        "teamMember", "Already Assigned");
                return errorModel;
            }

            TeamMemberEntity teamMemberEntity = new TeamMemberEntity();
            Util.copyProperty(teamMember, teamMemberEntity);

            String id = String.valueOf(redisUtil.getNextId(RedisKey.TEAM_MEMBER_KEY, RedisKey.TEAM_MEMBER_INITIAL_VALUE));
            teamMemberEntity.setId(id);
            teamMemberEntity.setProjectId(request.getProjectId());
            teamMemberEntity.setProjectName(request.getProjectName());
            teamMemberEntity.setVersionId(1l);
            teamMemberEntity.setStatus(Status.ACTIVE.name());
            teamMemberEntity.setTimeAndUser(username);

            teamMemberRepository.save(teamMemberEntity);

            updateTeamMemberHistory(teamMemberEntity);

            log.debug(teamMember.getFirstName() + " assigned to the project");
        } catch (Exception e) {
            log.error("Error occurred adding team member " + teamMember.getFirstName()
                    + " Error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("Error occurred adding team member "
                            + teamMember.getFirstName(), null, Locale.getDefault()),
                    "teamMember", e.getMessage());
            return errorModel;
        }
        return null;
    }

    private ErrorModel updateTeamMember(TeamMember teamMember, TeamMemberMappingRequest request, String username) {
        try {
            TeamMemberEntity teamMemberEntity = teamMemberRepository.findFirstByIdAndProjectIdAndStatus(
                    teamMember.getId(), request.getProjectId(), Status.ACTIVE.name());

            if (Objects.isNull(teamMemberEntity)) {
                log.error("Team member not found for " + teamMember.getFirstName());
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("Team member not found for " + teamMember.getFirstName(),
                                null, Locale.getDefault()),
                        "teamMember", "Not found");
                return errorModel;
            }

            Util.copyProperty(teamMember, teamMemberEntity);
            teamMemberEntity.setProjectId(request.getProjectId());
            teamMemberEntity.setProjectName(request.getProjectName());
            teamMemberEntity.setVersionId(teamMemberEntity.getVersionId() + 1);
            teamMemberEntity.setTimeAndUser(username);

            teamMemberRepository.save(teamMemberEntity);

            updateTeamMemberHistory(teamMemberEntity);

            log.debug(teamMember.getFirstName() + " updated");
        } catch (Exception e) {
            log.error("Error occurred updating team member " + teamMember.getFirstName()
                    + " Error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("Error occurred updating team member "
                            + teamMember.getFirstName(), null, Locale.getDefault()),
                    "teamMember", e.getMessage());
            return errorModel;
        }
        return null;
    }

    private ErrorModel deleteTeamMember(TeamMember teamMember, TeamMemberMappingRequest request, String username) {
        try {
            TeamMemberEntity teamMemberEntity = teamMemberRepository.findFirstByIdAndProjectIdAndStatus(
                    teamMember.getId(), request.getProjectId(), Status.ACTIVE.name());

            if (Objects.isNull(teamMemberEntity)) {
                log.error("Team member not found for " + teamMember.getFirstName());
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("Team member not found for " + teamMember.getFirstName(),
                                null, Locale.getDefault()),
                        "teamMember", "Not found");
                return errorModel;
            }

            teamMemberEntity.setStatus(Status.DELETED.name());
            teamMemberEntity.setVersionId(teamMemberEntity.getVersionId() + 1);
            teamMemberEntity.setTimeAndUser(username);

            teamMemberRepository.save(teamMemberEntity);

            updateTeamMemberHistory(teamMemberEntity);

            log.debug(teamMember.getFirstName() + " deleted");
        } catch (Exception e) {
            log.error("Error occurred deleting team member " + teamMember.getFirstName()
                    + " Error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("Error occurred deleting team member "
                            + teamMember.getFirstName(), null, Locale.getDefault()),
                    "teamMember", e.getMessage());
            return errorModel;
        }
        return null;
    }

    private void updateTeamMemberHistory(TeamMemberEntity teamMemberEntity) {
        TeamMemberHistoryEntity teamMemberHistoryEntity = new TeamMemberHistoryEntity();

        String id = String.valueOf(redisUtil.getNextId(RedisKey.TEAM_MEMBER_HISTORY_KEY,
                RedisKey.TEAM_MEMBER_HISTORY_INITIAL_VALUE));

        Util.copyProperty(teamMemberEntity, teamMemberHistoryEntity);
        teamMemberHistoryEntity.setId(id);
        teamMemberHistoryEntity.setTeamMemberIndexId(teamMemberEntity.getId());

        teamMemberHistoryRepository.indexWithoutRefresh(teamMemberHistoryEntity);

        log.debug(teamMemberEntity.getFirstName() + " history updated");
    }

    private List<ErrorModel> validateTeamMemberMapping(TeamMemberMappingRequest request, String userName) {
        List<ErrorModel> errorList = new ArrayList<>();
        try {
            ProjectEntity projectEntity = projectRepository.findByIdAndStatus(request.getProjectId(),
                    Status.ACTIVE.name());
            if (Objects.isNull(projectEntity)) {
                log.error("Project not found for " + request.getProjectId());
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("project.not.found", null, Locale.getDefault()),
                        "projectId", "Project not fond");
                errorList.add(errorModel);
            }

            if (Objects.nonNull(request.getProjectManager())) {
                if (userRepository.countById(request.getProjectManager().getUserId()) <= 0) {
                    log.error("Project manager not found");
                    ErrorModel errorModel = new ErrorModel(
                            messageSource.getMessage("user.not.found", null, Locale.getDefault()),
                            "projectManager", "No user found with provided id");
                    errorList.add(errorModel);
                    return errorList;
                }
            }

            if (Objects.nonNull(request.getProjectOwner())) {
                if (userRepository.countById(request.getProjectOwner().getUserId()) <= 0) {
                    log.error("Project owner not found");
                    ErrorModel errorModel = new ErrorModel(
                            messageSource.getMessage("user.not.found", null, Locale.getDefault()),
                            "projectOwner", "No user found with provided id");
                    errorList.add(errorModel);
                    return errorList;
                }
            }

            if (projectEntity.getProjectManager() != null || projectEntity.getProjectOwner() != null) {
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
            } else {
                if(Objects.isNull(request.getProjectOwner()) && Objects.isNull(request.getProjectManager())) {
                    log.error("You need to assign at least project owner or manager");
                    ErrorModel errorModel = new ErrorModel(
                            "You need to assign project owner or manager",
                            "project", "Bad Request");
                    errorList.add(errorModel);
                    return errorList;
                }
            }

            for (TeamMember teamMember : request.getTeamMemberList()) {
                if (userRepository.countByIdAndOrganizationId(teamMember.getUserId(), new BigInteger(teamMember.getOrganizationId())) <= 0) {
                    log.error("Team member not found for " + teamMember.getFirstName());
                    ErrorModel errorModel = new ErrorModel(
                            "NO user found for " + teamMember.getFirstName(),
                            "teamMember", "No user found with provided id and organization");
                    errorList.add(errorModel);
                }
            }
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            errorList.add(errorModel);
        }
        return errorList;
    }

    @Override
    public ServiceResponse list(TeamMemberListRequest request, String username) {
        try {
            List<ErrorModel> errorList = new ArrayList<>();

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

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, errorList);
            }

            NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();

            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

            queryBuilder.must(new QueryStringQueryBuilder(Status.ACTIVE.name()).field("status"));
            queryBuilder.must(new QueryStringQueryBuilder(request.getProjectId()).field("projectId"));

            FieldSortBuilder sort = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
            nsqb.withSort(sort);

            //PageRequest page = PageRequest.of(request.getPageNo(), request.getTotalItemPerPage());
            //nsqb.withPageable(page);

            SearchQuery searchQuery = nsqb.withQuery(queryBuilder)
                    .withFields("id", "userId", "firstName", "lastName", "domainName","organizationId",
                                "email", "userRoleInProject")
                    .build();

            List<TeamMemberEntity> teamMemberList = elasticsearchTemplate.queryForList(searchQuery, TeamMemberEntity.class);

            log.debug("Team member list size " + teamMemberList.size());
            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, teamMemberList, Collections.emptyList());
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
