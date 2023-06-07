package com.tigerit.soa.controller;

import com.tigerit.soa.entity.es.DecisionLog;
import com.tigerit.soa.request.DecisionLogListRequest;
import com.tigerit.soa.request.DecisionLogRequest;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.util.RedisUtil;
import com.tigerit.soa.util.Status;
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

/*
Fahim created at 5/7/2020
*/
@RestController
@RequestMapping("/decision-log")
@Log4j2
public class DecisionLogController {

    @Autowired
    RedisUtil redisUtil;

    List<DecisionLog> decisionLogList = new ArrayList<>();

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> decisionLogCreate(@RequestBody DecisionLogRequest request) {

        ServiceResponse response = new ServiceResponse();

        try {
            DecisionLog decisionLog = new DecisionLog();
            Util.copyProperty(request, decisionLog);

            decisionLog.setId(String.valueOf(redisUtil.getNextId("DECISION_LOG_DEMO",1)));
            decisionLog.setStatus(Status.ACTIVE.name());
            decisionLogList.add(decisionLog);

            log.info("Decison Log for project id " + request.getProjectId());

            response.setBody(true);
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

    @RequestMapping(path = "/decisionLogList", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponseExtended> decisionLogList(@Valid @RequestBody DecisionLogListRequest request,
                                                               BindingResult bindingResult) {

        ServiceResponseExtended response = new ServiceResponseExtended();
        try {
            if (bindingResult.hasErrors()) {
                log.error("Decision log list validation failed");
                ServiceResponse serviceResponse = errorHandler(bindingResult);
                response.buildServiceResponseExtended(serviceResponse);
                return ResponseEntity.ok(response);
            }

            List decisionLogListByProject = decisionLogList.stream()
                    .filter(r -> r.getProjectId().equals(request.getProjectId()))
                    .collect(Collectors.toList());

            log.info("Search result size " + decisionLogListByProject.size()+
                    " for project id " + request.getProjectId());

            response.setBody(decisionLogListByProject);
            response.setStatusCode(StatusCode.SUCCESS);
            response.setStatus(HttpStatus.OK);
            response.setTotalHits(decisionLogListByProject.size());
        } catch (Exception e) {
            log.error("Error occurresd " + e.getMessage());
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
