package com.tigerit.soa.entity.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tigerit.soa.model.es.TeamMember;
import com.tigerit.soa.model.es.UserDefineProperty;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DIPU on 5/12/20
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "project", type = "project")
@EqualsAndHashCode
public class ProjectEntity extends CommonProperty implements Serializable {
    @Id
    private String id;
    private String projectName;
    private String description;
    private String parentProjectId;
    private Long projectOwner;
    private String projectOwnerName;
    private Long projectManager;
    private String projectManagerName;
    private String departmentId;
    private String status;

    @Field(type = FieldType.Nested)
    private List<ActionStatusEntity> actionStatusList= new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<TeamMember> teamMemberList;

    @Field(type = FieldType.Nested)
    private List<UserDefineProperty> riskList;

}
