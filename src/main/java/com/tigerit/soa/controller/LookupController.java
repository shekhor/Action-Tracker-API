package com.tigerit.soa.controller;

import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.service.LookupService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*
Fahim created at 5/10/2020
*/
@RestController
@RequestMapping("/lookup")
@Log4j2
public class LookupController {

    @Autowired
    LookupService lookupService;

    @RequestMapping(path = "/actionStatusList", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponseExtended> actionStatusList() {

        ServiceResponseExtended response;
        String userName = "Fahim";
        response = lookupService.getActionStatusList(userName);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/riskLookupList", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponseExtended> riskLookupList() {

        ServiceResponseExtended response;
        String userName = "Fahim";
        response = lookupService.getRiskLookupList(userName);

        return ResponseEntity.ok(response);
    }
}
