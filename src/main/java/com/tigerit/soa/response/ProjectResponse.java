package com.tigerit.soa.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tigerit.soa.entity.es.CommonProperty;
import com.tigerit.soa.model.es.UserDefineProperty;
import com.tigerit.soa.request.es.ActionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by DIPU on 5/14/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectResponse extends CommonProperty implements Serializable {

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
    private List<ActionStatus> actionStatusList;
    private List<String> teamMemberList;
    private List<UserDefineProperty> riskList;
}
