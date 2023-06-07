package com.tigerit.soa.service;

import com.tigerit.soa.request.es.ActionStatusRequest;
import com.tigerit.soa.request.es.ProjectRequest;
import com.tigerit.soa.request.es.ProjectUpdateRequest;
import com.tigerit.soa.response.ServiceResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Locale;

/**
 * Created by DIPU on 5/14/20
 */
public interface ProjectService {

    ServiceResponse createProject(ProjectRequest projectRequest, UserDetails userDetails);
    ServiceResponse addActionstatusToProject(ActionStatusRequest actionStatus);

    ServiceResponse updateProject(ProjectUpdateRequest projectUpdateRequest,String projectId, String userName);
    ServiceResponse getProjectDetailsById(String projectId);

    ServiceResponse getActionstatusListByProjectId(String projectId);

    ServiceResponse getAllProjects(String username);

    ServiceResponse archiveProject(String projectId, String operatorName);

}
