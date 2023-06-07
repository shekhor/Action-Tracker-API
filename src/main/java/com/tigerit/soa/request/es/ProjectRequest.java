package com.tigerit.soa.request.es;

import com.tigerit.soa.model.es.UserDefineProperty;
import com.tigerit.soa.util.Defs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by DIPU on 5/14/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    //TODO: change the max min value based on requirement
    @Size(max = Defs.STR_MAX_LEN)
    private String id;
    @NotNull
    @NotEmpty
    @Size(min=Defs.STR_MIN_LEN, max = Defs.STR_MAX_LEN)
    private String projectName;
    @NotNull
    @NotEmpty
    @Size(min=Defs.STR_MIN_LEN, max = Defs.STR_MAX_LEN)
    private String description;
    private String parentProjectId;
    private Long projectOwner;
    private String projectOwnerName;
    private Long projectManager;
    private String projectManagerName;

    @NotNull
    @NotEmpty
    @Size(min=Defs.STR_MIN_LEN, max = Defs.STR_MAX_LEN)
    private String departmentId;
    @Size(max = Defs.STR_MAX_LEN)
    private String status;
    private List<ActionStatus> actionStatusList;

    //private List<String> teamMemberList;
    private List<UserDefineProperty> riskList;

}
