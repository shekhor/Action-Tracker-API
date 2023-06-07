package com.tigerit.soa.controller;

import com.tigerit.soa.entity.es.Risk;
import com.tigerit.soa.request.*;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.util.RedisUtil;
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
Fahim created at 5/5/2020
*/
@RestController
@RequestMapping("/risk")
@Log4j2
public class RiskController {

    @Autowired
    RedisUtil redisUtil;

    List<RiskRegistrationRequest> userDefinePropertyList = new ArrayList<>();
    List<Risk> riskList = new ArrayList<>();

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> riskRegistration(@RequestBody RiskRegistrationRequest request) {

        ServiceResponse response = new ServiceResponse();

        try {
            userDefinePropertyList.add(request);
            log.info("Risk registered for project id " + request.getProjectId());

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

    @RequestMapping(path = "/registeredRiskList", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getRegisteredRiskList(@Valid @RequestBody RegisteredRiskListRequest request,
                                                                    BindingResult bindingResult) {

        ServiceResponse response = new ServiceResponse();
        try {
            if (bindingResult.hasErrors()) {
                log.error("risk list validation failed");
                response = errorHandler(bindingResult);
                return ResponseEntity.ok(response);
            }

            List registeredRiskList = userDefinePropertyList.stream()
                    .filter(m -> m.getProjectId().equals(request.getProjectId()))
                    .collect(Collectors.toList());

            log.info("Search result size " + registeredRiskList.size()+ " for project id " + request.getProjectId());

            response.setBody(registeredRiskList);
            response.setStatusCode(StatusCode.SUCCESS);
            response.setStatus(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurresd " + e.getMessage());
            response.setStatusCode(StatusCode.ERROR);
            response.setErrorList(new ArrayList<String>(Arrays.asList(e.getMessage())));
        }
        return ResponseEntity.ok(response);
    }


    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> riskCreate(@RequestBody RiskCreateRequest request) {

        ServiceResponse response = new ServiceResponse();

        try {
            Risk risk = new Risk();
            risk.setProjectId(request.getProjectId());
            risk.setId(String.valueOf(redisUtil.getNextId("RISK_DEMON",1)));

            for(int i=0;i<request.getRegisteredRiskList().size();i++) {
                RiskRegistrationRequest data = request.getRegisteredRiskList().get(i);

                UserDefineProperty property = new UserDefineProperty();
                property.setHeader(data.getHeader());
                property.setBody(data.getBody());
                property.setFooter(data.getFooter());

                switch (data.getColumnNo()) {
                    case "column1":
                        property.setColumnNo("column1");
                        risk.setColumn1(property);
                        break;
                    case "column2":
                        property.setColumnNo("column2");
                        risk.setColumn2(property);
                        break;
                    case "column3":
                        property.setColumnNo("column3");
                        risk.setColumn3(property);
                        break;
                    case "column4":
                        property.setColumnNo("column4");
                        risk.setColumn4(property);
                        break;
                    default: log.info("No column matched");
                }
            }
            riskList.add(risk);

            log.info("Risk registered for project id " + request.getProjectId());

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

    @RequestMapping(path = "/riskList", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponseExtended> getRiskList(@Valid @RequestBody RiskListRequest request,
                                                                    BindingResult bindingResult) {

        ServiceResponseExtended response = new ServiceResponseExtended();
        try {
            if (bindingResult.hasErrors()) {
                log.error("risk list validation failed");
                ServiceResponse serviceResponse = errorHandler(bindingResult);
                response.buildServiceResponseExtended(serviceResponse);
                return ResponseEntity.ok(response);
            }

            List riskListByProject = riskList.stream()
                    .filter(r -> r.getProjectId().equals(request.getProjectId()))
                    .collect(Collectors.toList());

            log.info("Search result size " + riskListByProject.size()+ " for project id " + request.getProjectId());

            response.setBody(riskListByProject);
            response.setStatusCode(StatusCode.SUCCESS);
            response.setStatus(HttpStatus.OK);
            response.setTotalHits(riskListByProject.size());
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
