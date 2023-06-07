package com.tigerit.soa.service;

import com.tigerit.soa.request.GetOrganizationRequest;
import com.tigerit.soa.request.OrganizationCreateRequest;
import com.tigerit.soa.request.OrganizationUpdateRequest;
import com.tigerit.soa.response.ServiceResponse;

public interface OrganizationService {

    ServiceResponse createOrganization(OrganizationCreateRequest request, String userName);

    ServiceResponse updateOrganization(OrganizationUpdateRequest request, String userName);

    ServiceResponse deleteOrganization(OrganizationUpdateRequest request, String userName);

    ServiceResponse getAllOrganization(GetOrganizationRequest request, String userName);

    ServiceResponse getOrganizationById(String organizationId, String userName);
}
