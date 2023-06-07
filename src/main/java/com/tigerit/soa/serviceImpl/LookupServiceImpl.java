package com.tigerit.soa.serviceImpl;

import com.tigerit.soa.entity.ActionStatusEntity;
import com.tigerit.soa.entity.RiskLookupEntity;
import com.tigerit.soa.repository.ActionStatusRepository;
import com.tigerit.soa.repository.RiskLookupRepository;
import com.tigerit.soa.response.ErrorModel;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.LookupService;
import com.tigerit.soa.util.Status;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
Fahim created at 5/10/2020
*/
@Log4j2
@Service
public class LookupServiceImpl implements LookupService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ActionStatusRepository actionStatusRepository;

    @Autowired
    RiskLookupRepository riskLookupRepository;

    @Override
    public ServiceResponseExtended getActionStatusList(String userName) {
        try {
            log.debug("Getting action status list for " + userName);
            ServiceResponseExtended responseExtended = new ServiceResponseExtended();

            List<ActionStatusEntity> actionStatusList = actionStatusRepository
                    .findAllByStatus(Status.ACTIVE.name());

            responseExtended.setBody(actionStatusList);
            responseExtended.setTotalHits(actionStatusList.size());
            responseExtended.setStatusCode(StatusCode.SUCCESS);
            responseExtended.setStatus(HttpStatus.OK);

            log.debug("Action status list fetch completed");

            return responseExtended;
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);
            ServiceResponseExtended responseExtended = new ServiceResponseExtended();
            responseExtended.buildServiceResponseExtended(new ServiceResponse(HttpStatus.OK,
                    StatusCode.SUCCESS, null, errorList));
            return responseExtended;
        }
    }

    @Override
    public ServiceResponseExtended getRiskLookupList(String userName) {
        try {
            log.debug("Getting Risk Lookup list for " + userName);
            ServiceResponseExtended responseExtended = new ServiceResponseExtended();

            List<RiskLookupEntity> riskLookupList = riskLookupRepository
                    .findAllByStatus(Status.ACTIVE.name());

            responseExtended.setBody(riskLookupList);
            responseExtended.setTotalHits(riskLookupList.size());
            responseExtended.setStatusCode(StatusCode.SUCCESS);
            responseExtended.setStatus(HttpStatus.OK);

            log.debug("Risk Lookup list fetch completed");

            return responseExtended;
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);
            ServiceResponseExtended responseExtended = new ServiceResponseExtended();
            responseExtended.buildServiceResponseExtended(new ServiceResponse(HttpStatus.OK,
                    StatusCode.SUCCESS, null, errorList));
            return responseExtended;
        }
    }
}
