package com.tigerit.soa.controller;

import com.tigerit.soa.loginsecurity.util.SessionKey;
import com.tigerit.soa.model.es.ProjectNote;
import com.tigerit.soa.model.es.ProjectNoteSearchCriteria;
import com.tigerit.soa.request.es.ActionStatusRequest;
import com.tigerit.soa.request.es.ProjectNoteRequest;
import com.tigerit.soa.request.es.ProjectRequest;
import com.tigerit.soa.request.es.ProjectUpdateRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.ProjectService;
import com.tigerit.soa.util.Defs;
import com.tigerit.soa.util.TempoStaticDataProvider;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by DIPU on 5/5/20
 */

@RestController
@RequestMapping("/project")
@Log4j2
public class ProjectController {

    //private Logger log = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    ProjectService projectService;

    @Autowired
    MessageSource messageSource;

    @PostMapping("/create")
    public ResponseEntity<ServiceResponse> createProject(HttpServletRequest request, @Valid @RequestBody ProjectRequest projectRequest,
                                                         BindingResult bindingResult)
    {
        ServiceResponse response;
        if(bindingResult.hasErrors())
        {
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        log.info("create a project: service starts");
        response=projectService.createProject(projectRequest, userDetails);
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/update-header/{projectId}")
    public ResponseEntity<ServiceResponse> updateProjectHeader(HttpServletRequest request, @PathVariable("projectId")@NotBlank @Size(max = Defs.PROJECT_ID_MAX_LENGTH) String projectId,
                                                               @Valid @RequestBody ProjectUpdateRequest projectUpdateRequest,
                                                               BindingResult bindingResult)
    {
        ServiceResponse response;
        if(bindingResult.hasErrors())
        {
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        log.info("update project header start- by: {}", userDetails.getUsername());
        response=projectService.updateProject(projectUpdateRequest, projectId, userDetails.getUsername());
        log.info("update project header start- by: {}");
        return ResponseEntity.ok(response);
    }

    //TODO: need to prevent url tricks here, need more info from client
    @GetMapping("/{projectId}")
    public ResponseEntity<ServiceResponse> getProjectDetailsById(HttpServletRequest request , @PathVariable("projectId") @NotBlank @Size(max = Defs.PROJECT_ID_MAX_LENGTH) String projectId)
    {
        ServiceResponse response= new ServiceResponse();
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        log.info("project details fetch start: project id: {}", projectId);
        response=projectService.getProjectDetailsById(projectId);
        log.info("project details fetch end");
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/action-status/add")
    public ResponseEntity<ServiceResponse> addActionStatusToProject(HttpServletRequest request, @Valid @RequestBody ActionStatusRequest actionStatusRequest, BindingResult bindingResult)
    {
        ServiceResponse response;
        if(bindingResult.hasErrors())
        {
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        log.info("action status added by user: {}", userDetails.getUsername());
        log.info("action status: service start");
        response= projectService.addActionstatusToProject(actionStatusRequest);
        log.info("action status: service end");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/action-status-list/{projectId}")
    public ResponseEntity<ServiceResponse> getActionStatusList(HttpServletRequest request,
                                                               @PathVariable("projectId") @NotBlank @Size(max = Defs.PROJECT_ID_MAX_LENGTH) String projectId)
    {
        ServiceResponse response;
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);

        log.info("get actionStatusList of projectId: {} ---start", projectId);
        response=projectService.getActionstatusListByProjectId(projectId);
        log.info("getActionStatusList fetch: end");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/archive/{projectId}")
    public ResponseEntity<ServiceResponse> archiveProject(HttpServletRequest request,
                                                          @PathVariable("projectId")@NotBlank @Size(max = 255) String projectId)
    {
        ServiceResponse response;
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);

        log.info("project archiving: start");
        response=projectService.archiveProject(projectId, userDetails.getUsername());
        log.info("project archiving: end");
        return ResponseEntity.ok(response);
    }


    /*
    this servic3e is for testing purpose only:
    TODO: will be transformed to fetch all for specific user later
     */
    @GetMapping("/get-all")
    public ResponseEntity<ServiceResponse> getAllProjects(HttpServletRequest request)
    {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        ServiceResponse response= projectService.getAllProjects(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/note/create")
    public ResponseEntity<ServiceResponse> createNote(HttpServletRequest request, @Valid @RequestBody ProjectNoteRequest noteRequest,
                                                      BindingResult bindingResult)
    {
        ServiceResponse response;
        if(bindingResult.hasErrors())
        {
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        //operations here:
        log.info("create a project note: service starts");
        //TODO: service call here:
        response=new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, TempoStaticDataProvider.getDataForCreateProjectNote(noteRequest), Collections.emptyList());

        log.info("create a project note: service ends");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/note/search")
    public ResponseEntity<ServiceResponseExtended> getNotes(HttpServletRequest request, ProjectNoteSearchCriteria noteSearchCriteria,
                                                            BindingResult bindingResult,
                                                            @RequestParam(defaultValue = "0") int pageNo,
                                                            @RequestParam(defaultValue = "10") int pageSize,
                                                            @RequestParam(defaultValue = "id") String sortBy)
    {
        ServiceResponseExtended responseExtended = new ServiceResponseExtended<>();


        if(bindingResult.hasErrors())
        {
            ServiceResponse response;
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(responseExtended.buildServiceResponseExtended(response));
        }

        //operations here:
        log.info("search for project note: service starts");
        //TODO: service call here:
        List<ProjectNote>noteList= TempoStaticDataProvider.getProjectNoteList();

        responseExtended=new ServiceResponseExtended(HttpStatus.OK, StatusCode.SUCCESS,
                noteList, new Long(pageNo), new Long(pageSize), new Long(noteList.size()));

        log.info("search for project note: service ends");
        return ResponseEntity.ok(responseExtended);
    }


    private ServiceResponse errorHandler(BindingResult bindingResult) {
        ServiceResponse response =new ServiceResponse();
        List<ErrorModel> errorModelList = new ArrayList();

        for(FieldError fieldError : bindingResult.getFieldErrors()) {
            ErrorModel errorModel = new ErrorModel();
            errorModel.setField(fieldError.getField());
            errorModel.setMessage(fieldError.getDefaultMessage());
            errorModel.setDescription(fieldError.getObjectName());
            errorModelList.add(errorModel);
        }

        response.setErrorList(errorModelList);
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setStatusCode(StatusCode.ERROR);
        return response;
    }
}
