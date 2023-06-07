package com.tigerit.soa.controller;

import com.tigerit.soa.request.MilestoneCreateRequest;
import com.tigerit.soa.request.MilestoneListRequest;
import com.tigerit.soa.response.*;
import com.tigerit.soa.util.RedisUtil;
import com.tigerit.soa.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/milestone")
@Log4j2
public class MilestoneController {

    @Autowired
    RedisUtil redisUtil;

    List<MilestoneResponse> milestoneResponseList = new ArrayList<>();

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> milestoneCreate(@RequestBody MilestoneCreateRequest request) {

        ServiceResponse response = new ServiceResponse();

        try {
            MilestoneResponse milestoneResponse = new MilestoneResponse();
            Util.copyProperty(request, milestoneResponse);
            milestoneResponse.setId(String.valueOf(redisUtil.getNextId("MILESTONE_DEMO", 1)));
            milestoneResponse.setSuccess(true);

            milestoneResponseList.add(milestoneResponse);

            log.info("Milestone created for project id " + request.getProjectId());

            response.setBody(milestoneResponse);
            response.setStatus(HttpStatus.OK);
            response.setStatusCode(StatusCode.SUCCESS);
            response.setErrorList(Collections.emptyList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurresd " + e.getMessage());
            response.setStatusCode(StatusCode.ERROR);
            response.setErrorList(new ArrayList<String>(Arrays.asList(e.getMessage())));
        }

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/milestoneList", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponseExtended> getMilestoneList(@Valid @RequestBody MilestoneListRequest request,
                                                                    BindingResult bindingResult) {

        ServiceResponseExtended response = new ServiceResponseExtended();
        try {
            if (bindingResult.hasErrors()) {
                log.error("Organization create validation failed");
                ServiceResponse serviceResponse = errorHandler(bindingResult);
                response.buildServiceResponseExtended(serviceResponse);
                return ResponseEntity.ok(response);
            }

            List milestoneList = milestoneResponseList.stream()
                    .filter(m -> m.getProjectId().equals(request.getProjectId()))
                    .collect(Collectors.toList());

            log.info("Search result size " + milestoneList.size()+ " for project id " + request.getProjectId());

            response.setBody(milestoneList);
            response.setStatusCode(StatusCode.SUCCESS);
            response.setStatus(HttpStatus.OK);
            response.setTotalHits(milestoneList.size());
        } catch (Exception e) {
            log.error("Error occurred " + e.getMessage());
            response.setStatusCode(StatusCode.ERROR);
            response.setErrorList(new ArrayList<String>(Arrays.asList(e.getMessage())));
        }
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
