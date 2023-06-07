package com.tigerit.soa.service;

import com.tigerit.soa.response.ServiceResponseExtended;

public interface LookupService {

    ServiceResponseExtended getActionStatusList(String userName);

    ServiceResponseExtended getRiskLookupList(String userName);
}
