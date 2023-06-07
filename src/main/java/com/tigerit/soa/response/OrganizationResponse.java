package com.tigerit.soa.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
Fahim created at 4/9/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse implements Serializable {

    private String id;
    private String organizationName;
    private String organizationOwner;
    private String orgCode;
    private String address;
    private String description;
    private String domainName;
}
