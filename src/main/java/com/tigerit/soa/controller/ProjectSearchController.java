package com.tigerit.soa.controller;

import com.tigerit.soa.loginsecurity.util.SessionKey;
import com.tigerit.soa.request.project.ProjectListRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.ProjectSearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/*
Fahim created at 5/17/2020
*/
@RestController
@RequestMapping("/project-search")
@Log4j2
public class ProjectSearchController {

    @Autowired
    ProjectSearchService projectSearchService;

    @RequestMapping(path = "/list", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponseExtended> projectList(HttpServletRequest httpRequest,
                                                               @Valid @RequestBody ProjectListRequest request,
                                                               BindingResult bindingResult) {

        ServiceResponseExtended responseExtended = new ServiceResponseExtended<>();

        if(bindingResult.hasErrors())
        {
            ServiceResponse response = errorHandler(bindingResult);
            return ResponseEntity.ok(responseExtended.buildServiceResponseExtended(response));
        }

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        responseExtended = projectSearchService.list(request, username);

        return ResponseEntity.ok(responseExtended);
    }

    private ServiceResponse errorHandler(BindingResult bindingResult) {
        ServiceResponse response = new ServiceResponse();
        List<ErrorModel> errorModelList = new ArrayList();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
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
