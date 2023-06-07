package com.tigerit.soa.serviceImpl;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.entity.es.ActionStatusEntity;
import com.tigerit.soa.entity.es.ProjectEntity;
import com.tigerit.soa.loginsecurity.repository.UserRepository;
import com.tigerit.soa.model.es.ActionStatus;
import com.tigerit.soa.repository.es.ProjectRepository;
import com.tigerit.soa.request.es.ActionStatusRequest;
import com.tigerit.soa.request.es.ProjectRequest;
import com.tigerit.soa.request.es.ProjectUpdateRequest;
import com.tigerit.soa.response.ProjectResponse;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.ProjectService;
import com.tigerit.soa.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by DIPU on 5/14/20
 */

//TODO: consider url hacking for any post/write operation--- upon discussion/finalization later

@Service
public class ProjectServiceImpl implements ProjectService {

    private Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public ServiceResponse createProject(ProjectRequest projectRequest, UserDetails userDetails) {

        try
        {
            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(userDetails.getUsername());
            String projectId = String.valueOf(redisUtil.getNextId(RedisKey.PROJECT_KEY,
                    RedisKey.PROJECT_INITIAL_KEY_VALUE));

            ProjectEntity projectEntity= new ProjectEntity();
            Util.copyProperty(projectRequest, projectEntity);
            projectEntity.setId(projectId);

            projectEntity.setCreatedBy(userDetails.getUsername());
            projectEntity.setCreateTime(new Date());
            projectEntity.setCreateTimeInMs((new Date()).getTime());
            projectEntity.setStatus(Status.ACTIVE.name());
            projectEntity.setVersionId(Defs.VERSION_ID);

            projectEntity.setProjectOwner(userEntityOptional.get().getId());
            projectEntity.setProjectOwnerName(userDetails.getUsername());

            logger.info("copy project request data to entity");


            projectEntity= projectRepository.save(projectEntity);
            logger.info("------ new project created------");

            ProjectResponse responseBody=new ProjectResponse();
            Util.copyProperty(projectEntity, responseBody);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, responseBody, Collections.emptyList());
        }
        catch (Exception e)
        {
            logger.error("Error:"+ e.getMessage());
        }

        logger.debug("---project create: error, check log for details ");
        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("Internal Error, please try again!")));
    }

    @Override
    public ServiceResponse addActionstatusToProject(ActionStatusRequest actionStatus) {

        if(actionStatus.getActionStatusList()!=null && actionStatus.getActionStatusList().size() <=0)
            return new ServiceResponse(HttpStatus.EXPECTATION_FAILED, StatusCode.ERROR, null,
                    new ArrayList<String>(Arrays.asList("Please provide a valid nonempty list!")));


        logger.info("check if projectId: {} exists", actionStatus.getProjectId());

        //TODO: may be add u.ID too with P.ID to protect url trick? discuss
        ProjectEntity projectEntity= projectRepository.findByIdAndStatus(actionStatus.getProjectId(), Status.ACTIVE.name());
        if(!Objects.isNull(projectEntity))
        {
            logger.info("project found with id: {}", actionStatus.getProjectId());

            //ProjectEntity projectEntity= entityOptional.get();

            try
            {
                List<ActionStatusEntity> actionStatusEntityList= new ArrayList<>();
                actionStatusEntityList= Util.toDtoList(actionStatus.getActionStatusList(), ActionStatusEntity.class);

                projectEntity.setActionStatusList(actionStatusEntityList);

                logger.info("update project actionstatus list---");
                projectRepository.save(projectEntity);

                ProjectResponse responseBody=new ProjectResponse();
                Util.copyProperty(projectEntity, responseBody);

                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, responseBody, Collections.emptyList());
            }
            catch (Exception e)
            {
                logger.error("Error updating actionStatus list: "+ e.getMessage());
            }

            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null,
                    new ArrayList<String>(Arrays.asList("Internal error! Please try again later")));
        }

        logger.error("No project found with id:{} ", actionStatus.getProjectId());

        return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("Invalid projectId provided, please provide a valid one")));
    }

    @Override
    public ServiceResponse updateProject(ProjectUpdateRequest projectUpdateRequest, String projectId, String userName) {

        logger.info("check if projectId: {} exists to update header", projectId);
       try
       {
           ProjectEntity projectEntity= projectRepository.findByIdAndStatus(projectId, Status.ACTIVE.name());

           if(!Objects.isNull(projectEntity)) {
               logger.info("project found with id: {}", projectId);
              // ProjectEntity projectEntity = entityOptional.get();
               //TODO: need security check for cross org user

               projectEntity.setProjectName(projectUpdateRequest.getProjectHeader());
               projectEntity.setEditedBy(userName);
               projectEntity.setEditTime(new Date());
               projectEntity.setEditTimeInMs((new Date()).getTime());
               projectEntity.setVersionId(projectEntity.getVersionId()+1L);

               logger.info("update project header");
               projectEntity=projectRepository.save(projectEntity);

               ProjectResponse responseBody=new ProjectResponse();
               Util.copyProperty(projectEntity, responseBody);
               return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, responseBody, Collections.emptyList());
           }
           else
           {
               logger.info("no such project found to update project header...");
               return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null,
                       new ArrayList<String>(Arrays.asList("Project not found! Please create a project first!")));
           }
       }
       catch (Exception e)
       {
           logger.error("unwanted error:"+ e.getMessage());
       }

        logger.debug("Internal error updating header, check log details...");
        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("Internal Error, please try again!")));
    }

    @Override
    public ServiceResponse getProjectDetailsById(String projectId) {

        ProjectEntity projectEntity= projectRepository.findByIdAndStatus(projectId, Status.ACTIVE.name());
        if(!Objects.isNull(projectEntity))
        {
           // TODO: need to check if operator is from same org. does that mean in a project, everybody will be from same org??  how about devs working with diff org, then how to handle this?
            ProjectResponse responseBody= new ProjectResponse();

            logger.info("fetching and sending project details...");
            Util.copyProperty(projectEntity, responseBody);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, responseBody, Collections.emptyList());
        }

        logger.info("project not found with id: {}", projectId);
        return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("Project not found!")));
    }

    @Override
    public ServiceResponse getActionstatusListByProjectId(String projectId) {

        try
        {
            //TODO: do I need to fetch project by id and u.id too for better security/to avoid url trick?-discuss
            ProjectEntity projectEntity= projectRepository.findByIdAndStatus(projectId, Status.ACTIVE.name());
            if(!Objects.isNull(projectEntity))
            {
                logger.info("project found with actionstatus-p.Id: {}", projectId);
                List<ActionStatusEntity> actionStatusEntityList=projectEntity.getActionStatusList();
                List<ActionStatus> actionStatusList;
                actionStatusList=Util.toDtoList(actionStatusEntityList, ActionStatus.class);
                if(actionStatusList!=null && actionStatusList.size() >0)
                    logger.info("actionStatusList size: {}", actionStatusList.size());

                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, actionStatusList, Collections.emptyList());
            }
            else
            {
                logger.info("no project found with id: {} for actionstatuslist", projectId);
                return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null,
                        new ArrayList<String>(Arrays.asList("Project not found!")));
            }
        }
        catch (Exception e)
        {
            logger.error("error:"+ e.getMessage());
        }

        logger.debug("Internal Error fetching actionstatuslist with projectId: {}", projectId);
        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("Internal Error, please try again!")));
    }

    @Override
    public ServiceResponse getAllProjects(String username) {
        Iterable<ProjectEntity> list= projectRepository.findAll();
        List<ProjectResponse> responseList;
        responseList=Util.toDtoList(list, ProjectResponse.class);
        return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, responseList, Collections.emptyList());
    }

    @Override
    public ServiceResponse archiveProject(String projectId, String operatorName) {

        //TODO: there will be more validation like, if there is any open/incomplete task of this project or its child projects
       ProjectEntity project=projectRepository.findByIdAndStatus(projectId, Status.ACTIVE.name());
        try
        {
            if(!Objects.isNull(project))
            {

                //archive authority only on on projectManager and owner
                boolean bool=Util.isEqualIgnoredCaseStr(project.getProjectManagerName(), operatorName);
                boolean bool2=Util.isEqualIgnoredCaseStr(project.getProjectOwnerName(), operatorName);
                if(bool) logger.info("Archiving project with id: {}  by projectManager- {}", projectId, operatorName);
                if(bool2) logger.info("Archiving project with id: {}  by projectOwner- {}", projectId, operatorName);
                if(bool || bool2)
                {
                    project.setStatus(Status.ARCHIVED.name());
                    project=projectRepository.save(project);

                    logger.info("project archived for id: {}", projectId);
                    ProjectResponse responseBody= new ProjectResponse();
                    Util.copyProperty(project, responseBody);
                    return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, responseBody, Collections.emptyList());
                }
                else
                {
                    logger.debug("Unauthorized user operation-archive for P.Id: {}", projectId);
                    return new ServiceResponse(HttpStatus.FORBIDDEN, StatusCode.ERROR, null,
                            new ArrayList<String>(Arrays.asList("User must be projectOwner or projectManager to archive a project!")));
                }
            }
            else
            {
                logger.info("no project found with id: {}", projectId);
                return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null,
                        new ArrayList<String>(Arrays.asList("Project not found!")));
            }

        }
        catch (Exception e)
        {
            logger.error("archiving error:"+ e.getMessage());
        }

        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("Internal Error, please try again!")));
    }

}
