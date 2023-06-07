package com.tigerit.soa.controller;

import com.tigerit.soa.loginsecurity.util.SessionKey;
import com.tigerit.soa.model.es.TaskNote;
import com.tigerit.soa.model.es.TaskNoteSearchCriteria;
import com.tigerit.soa.request.es.TaskNoteRequest;
import com.tigerit.soa.request.task.TaskRequest;
import com.tigerit.soa.request.task.TaskSearchRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.TaskService;
import com.tigerit.soa.util.TempoStaticDataProvider;
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
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by DIPU on 5/7/20
 */

@RestController
@RequestMapping("/task")
public class TaskController {

    private Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    MessageSource messageSource;

    @Autowired
    TaskService taskService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> createTask(HttpServletRequest httpRequest,
                                                             @Valid @RequestBody TaskRequest request,
                                                             BindingResult bindingResult) {

        ServiceResponse response;

        if (bindingResult.hasErrors()) {
            log.error("Task creation validation failed");
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        response = taskService.createTask(request, username);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> updateTask(HttpServletRequest httpRequest,
                                                      @Valid @RequestBody TaskRequest request,
                                                      BindingResult bindingResult) {

        ServiceResponse response;

        if (bindingResult.hasErrors()) {
            log.error("Task update validation failed");
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        response = taskService.updateTask(request, username);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/delete/{projectId}/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> deleteTask(HttpServletRequest httpRequest,
                                                      @PathVariable("projectId") String projectId,
                                                      @PathVariable("taskId") String taskId) {

        ServiceResponse response;

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        response = taskService.deleteTask(taskId, projectId, username);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/detail/{projectId}/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> detailTask(HttpServletRequest httpRequest,
                                                      @NotEmpty @PathVariable("projectId") String projectId,
                                                      @NotEmpty @PathVariable("taskId") String taskId) {

        ServiceResponse response;

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        response = taskService.detailTask(taskId, projectId, username);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/list", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponseExtended> detailTask(HttpServletRequest httpRequest,
                                                      @Valid @RequestBody TaskSearchRequest request) {

        ServiceResponseExtended response;

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        response = taskService.actionList(request, username);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/search", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponseExtended> searchTask(HttpServletRequest httpRequest,
                                                              @Valid @RequestBody TaskSearchRequest request) {

        ServiceResponseExtended response;

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        response = taskService.searchTask(request, username);

        return ResponseEntity.ok(response);
    }



    @PostMapping("/note/create")
    public ResponseEntity<ServiceResponse> createNote(HttpServletRequest request, @Valid @RequestBody TaskNoteRequest taskNoteRequest, BindingResult bindingResult, Locale locale)
    {
        ServiceResponse response;
        if(bindingResult.hasErrors())
        {
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        //operations here:
        log.info("create a task note: service starts");
        //TODO: service call here:
        response=new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, TempoStaticDataProvider.getDataForCreateTaskNote(taskNoteRequest), Collections.emptyList());

        log.info("create a task note: service ends");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/note/search")
    public ResponseEntity<ServiceResponseExtended> getNotes(HttpServletRequest request, TaskNoteSearchCriteria searchCriteria, BindingResult bindingResult, Locale locale,
                                                            @RequestParam(defaultValue = "0") int pageNo,
                                                            @RequestParam(defaultValue = "10") int pageSize,
                                                            @RequestParam(defaultValue = "id") String sortBy)
    {
        ServiceResponseExtended responseExtended = new ServiceResponseExtended<>();

        //TODO: redundant may be ?
        if(bindingResult.hasErrors())
        {
            ServiceResponse response;
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(responseExtended.buildServiceResponseExtended(response));
        }

        //operations here:
        log.info("search for task note: service starts");
        //TODO: service call here:
        List<TaskNote>noteList= TempoStaticDataProvider.getTaskNoteList();

        responseExtended=new ServiceResponseExtended(HttpStatus.OK, StatusCode.SUCCESS,
                noteList, new Long(pageNo), new Long(pageSize), new Long(noteList.size()));

        log.info("search for task note: service ends");
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
