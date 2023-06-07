package com.tigerit.soa.controller;

import com.tigerit.soa.loginsecurity.util.SessionKey;
import com.tigerit.soa.model.PaginationInfo;
import com.tigerit.soa.request.es.DepartmentRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.DepartmentService;
import com.tigerit.soa.util.Defs;
import com.tigerit.soa.util.Util;
import lombok.extern.log4j.Log4j2;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by DIPU on 4/9/20
 */

@RestController
@RequestMapping("/department")
@Log4j2
public class DepartmentController {

    //private Logger log = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    DepartmentService departmentService;
    @Autowired
    MessageSource messageSource;

    @GetMapping("/test")
    public ResponseEntity<Object> testDB()
    {
        ServiceResponse response=departmentService.test();

       return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ServiceResponse> createDepartment(HttpServletRequest request, @Valid @RequestBody DepartmentRequest departmentRequest, BindingResult bindingResult, HttpServletResponse response_p, Locale locale)
    {
        ServiceResponse response;
        if(bindingResult.hasErrors())
        {
            response = Util.requestErrorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        log.info("create a department: service starts");
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        response=departmentService.createDepartment(departmentRequest,userDetails, locale);

        log.info("create a department: service ends");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ServiceResponse> editDepartment(HttpServletRequest request, @Valid @RequestBody DepartmentRequest departmentRequest, BindingResult bindingResult, Locale locale)
    {

        ServiceResponse response;
        if(Util.isEmpty(departmentRequest.getId()))
        {
            String errorMessage=messageSource.getMessage("department.id.invalid", null, locale);
            response=new ServiceResponse(HttpStatus.BAD_REQUEST, StatusCode.ERROR, null, new ArrayList<String>(Arrays.asList(errorMessage)));
            return ResponseEntity.ok(response);
        }
        if(bindingResult.hasErrors())
        {
            List<ErrorModel> errorModelList=new ArrayList<>();
            response=new ServiceResponse();
            for(FieldError error: bindingResult.getFieldErrors())
            {
                ErrorModel errorModel=new ErrorModel(messageSource.getMessage(error.getDefaultMessage(), null, locale),
                        error.getField(), error.getObjectName());
                errorModelList.add(errorModel);
            }

            response.setErrorList(errorModelList);
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setStatusCode(StatusCode.ERROR);
            return ResponseEntity.ok(response);
        }

        //update operation
        log.info("update a department: service starts");
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        response=departmentService.editDepartment(departmentRequest, userDetails, locale);

        log.info("update a department: service ends");
        return ResponseEntity.ok(response);
    }

    //TODO: need to prevent url tricks here, need more info from client, orgId too?
    @GetMapping("/{departmentId}")
    public ResponseEntity<ServiceResponse> getDepartmentDetailsById(HttpServletRequest request , @PathVariable("departmentId") @NotBlank @Size(max = Defs.DEPARTMENT_ID_MAX_LENGTH) String deptId)
    {
        ServiceResponse response= new ServiceResponse();
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        log.info("dept details fetch start: dept id: {}", deptId);
        response=departmentService.getDepartmentDetailsById(deptId, userDetails.getUsername());
        log.info("dept details fetch end");
        return  ResponseEntity.ok(response);
    }

    //TODO:need prod testing from new ES
    @PostMapping("/all")
    public ResponseEntity<ServiceResponseExtended> searchDepartment(HttpServletRequest request, @Valid @RequestBody PaginationInfo pageReq, BindingResult bindingResult, Locale locale)
    {
        ServiceResponseExtended responseExtended = new ServiceResponseExtended<>();


        if(bindingResult.hasErrors())
        {
            ServiceResponse response;
            response = Util.requestErrorHandler(bindingResult);
            return ResponseEntity.ok(responseExtended.buildServiceResponseExtended(response));
        }

        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        log.info("department list: service start-- calling by:{}", userDetails.getUsername());
        responseExtended=departmentService.getAllDepartmentPerOrg(pageReq, userDetails.getUsername());
         log.info("depart list fetch: service ends");
        return  ResponseEntity.ok(responseExtended);
    }


    @GetMapping("/archive/{departmentId}")
    public ResponseEntity<ServiceResponse> archiveDepartment(HttpServletRequest request,
                                                          @PathVariable("departmentId")@NotBlank @Size(max = 255) String departmentId) {
        ServiceResponse response;
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);

        log.info("department archiving: start");
        response=departmentService.archiveDepartment(departmentId, userDetails.getUsername());
        log.info("department archiving: end");
        return ResponseEntity.ok(response);
    }


}
