package com.tigerit.soa.entity.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.io.Serializable;

/*
Fahim created at 5/18/2020
*/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "organization_history", type = "organization_history")
@EqualsAndHashCode
public class OrganizationHistoryEntity extends CommonProperty implements Serializable {
    @Id
    private String id;
    private String organizationIndexId;
    private String organizationName;
    private String type;
    private String organizationOwner;
    private String orgCode;
    private String address;
    private String description;
    private String status;
    private String domainName;
}
