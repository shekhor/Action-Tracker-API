package com.tigerit.soa.entity.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

/*
Fahim created at 5/12/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(indexName = "team_member_history", type = "team_member_history")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamMemberHistoryEntity extends CommonProperty {

    @Id
    private String id;
    private String teamMemberIndexId;
    private Long userId;
    private String projectName;
    private String projectId;
    private String firstName;
    private String lastName;
    private String domainName;
    private String organizationId;
    private String userRoleInProject;
    private String status;
    private Long versionId;
}

