package com.tigerit.soa.controller;

import com.tigerit.soa.loginsecurity.util.SessionKey;
import com.tigerit.soa.request.category.CategoryListRequest;
import com.tigerit.soa.request.category.CategoryMappingRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.CategoryService;
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
Fahim created at 5/14/2020
*/
@RestController
@RequestMapping("/category")
@Log4j2
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @RequestMapping(path = "/mapping", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> teamMemberMappong(HttpServletRequest httpRequest,
                                                             @Valid @RequestBody CategoryMappingRequest request,
                                                             BindingResult bindingResult) {

        ServiceResponse response;

        if (bindingResult.hasErrors()) {
            log.error("Category mapping validation failed");
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        response = categoryService.mapping(request, username);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/list", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> teamMemberList(HttpServletRequest httpRequest,
                                                          @Valid @RequestBody CategoryListRequest request,
                                                          BindingResult bindingResult) {

        ServiceResponse response;

        if(bindingResult.hasErrors()) {
            log.error("Category list validation failed");
            response = errorHandler(bindingResult);
            return ResponseEntity.ok(response);
        }

        UserDetails userDetails = (UserDetails) httpRequest.getSession().getAttribute(SessionKey.USER_DETAILS);
        String username = userDetails.getUsername();
        response = categoryService.list(request, username);

        return ResponseEntity.ok(response);
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
