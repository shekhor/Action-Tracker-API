package com.tigerit.soa.service;

import com.tigerit.soa.request.teammember.TeamMemberListRequest;
import com.tigerit.soa.request.teammember.TeamMemberMappingRequest;
import com.tigerit.soa.response.ServiceResponse;

public interface TeamMemberService {

    ServiceResponse mapping(TeamMemberMappingRequest request, String username);

    ServiceResponse list(TeamMemberListRequest request, String username);
}
