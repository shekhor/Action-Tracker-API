package com.tigerit.soa.service;

import com.tigerit.soa.model.PaginationInfo;
import com.tigerit.soa.request.es.DepartmentRequest;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Locale;

public interface DepartmentService {

    ServiceResponse test();
    ServiceResponse createDepartment(DepartmentRequest departmentRequest, UserDetails userDetails, Locale locale);
    ServiceResponse editDepartment(DepartmentRequest departmentRequest, UserDetails userDetails, Locale locale);
    ServiceResponse getDepartmentDetailsById(String departmentId, String username);
    ServiceResponseExtended getAllDepartmentPerOrg(PaginationInfo pageReq, String userName);
    ServiceResponse archiveDepartment(String deptId, String userName);

}
