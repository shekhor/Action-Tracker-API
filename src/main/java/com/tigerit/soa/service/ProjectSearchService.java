package com.tigerit.soa.service;

import com.tigerit.soa.request.project.ProjectListRequest;
import com.tigerit.soa.response.ServiceResponseExtended;

public interface ProjectSearchService {
    ServiceResponseExtended list(ProjectListRequest request, String username);
}
