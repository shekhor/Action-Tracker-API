package com.tigerit.soa.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/*
Fahim created at 4/12/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUpdateRequest {

    @NotEmpty(message = "ID can not by null or empty")
    String id;
    @NotEmpty(message = "Organization name can not by null or empty")
    private String organizationName;
    @NotEmpty(message = "Organization owner can not by null or empty")
    private String organizationOwner;
    @NotEmpty(message = "Org code can not be null or empty")
    private String orgCode;
    private String address;
    private String description;
    @NotEmpty(message = "Domain name can not be null or empty")
    private String domainName;
}
