package com.tigerit.soa.response;

import com.tigerit.soa.entity.es.OrganizationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/*
Fahim created at 4/12/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOrganizationResponse implements Serializable {

    List<OrganizationEntity> organizationEntityList;
    long totalItem;
}
