package com.tigerit.soa.controller;

import com.tigerit.soa.loginsecurity.util.SessionKey;
import com.tigerit.soa.request.GetOrganizationRequest;
import com.tigerit.soa.request.OrganizationCreateRequest;
import com.tigerit.soa.request.OrganizationUpdateRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.OrganizationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/*
Fahim created at 4/9/2020
*/
@RestController
@RequestMapping("/organization")
@Log4j2
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> createOrganization(HttpServletRequest httpRequest,
                                           @Valid @RequestBody OrganizationCreateRequest request,
                                           BindingResult bindingResult) {

        ServiceResponse response;

        if(bindingResult.hasErrors()) {
            log.error("Organization create validation failed");
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String userName = userDetails.getUsername();
        response = organizationService.createOrganization(request, userName);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> updateOrganization(HttpServletRequest httpRequest,
                                           @Valid @RequestBody OrganizationUpdateRequest request,
                                           BindingResult bindingResult) {

        ServiceResponse response;

        if(bindingResult.hasErrors()) {
            log.error("Organization update validation failed");
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String userName = userDetails.getUsername();
        response = organizationService.updateOrganization(request, userName);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> deleteOrganization(HttpServletRequest httpRequest,
                                           @Valid @RequestBody OrganizationUpdateRequest request,
                                           BindingResult bindingResult) {

        ServiceResponse response;

        if(bindingResult.hasErrors()) {
            log.error("Organization update validation failed");
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        String userName = "system";
        response = organizationService.deleteOrganization(request, userName);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/getAll", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getAllOrganization(HttpServletRequest httpRequest,
                                           @Valid @RequestBody GetOrganizationRequest request,
                                           BindingResult bindingResult) {

        ServiceResponse response;

        if(bindingResult.hasErrors()) {
            log.error("Organization search validation failed");
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String userName = userDetails.getUsername();
        response = organizationService.getAllOrganization(request,userName);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/getOrganizationById/{organizationId}", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getAllOrganization(HttpServletRequest httpRequest,
                                           @PathVariable("organizationId") String organizationId) {

        ServiceResponse response;

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String userName = userDetails.getUsername();
        response = organizationService.getOrganizationById(organizationId, userName);

        return ResponseEntity.ok(response);
    }

    //Test code to test automatic organization creation
//    @RequestMapping(path = "/createOrg", method = RequestMethod.GET)
//    public ResponseEntity<ServiceResponse> testCreateOrg() {
//
//        ServiceResponse response;
//        OrganizationCreateRequest request = new OrganizationCreateRequest();
//        request.setDomainName("gmail.com");
//        request.setOrganizationOwner("Owner");
//
//        response = organizationService.createOrganization(request, "Owner");
//
//        OrganizationResponse organizationResponse = (OrganizationResponse) response.getBody();
//        System.out.println(organizationResponse.getId());
//
//        return ResponseEntity.ok(response);
//    }


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
