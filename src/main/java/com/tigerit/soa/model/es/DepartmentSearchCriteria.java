package com.tigerit.soa.model.es;

import com.tigerit.soa.model.PaginationInfo;
import com.tigerit.soa.util.Defs;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by DIPU on 6/3/20
 */

@Data
public class DepartmentSearchCriteria extends PaginationInfo implements Serializable {

    @Size(max= Defs.DEPARTMENT_ID_MAX_LENGTH)
    private String id;

    @Size(max= Defs.ES_PK_MAX_LEN)
    private String organizationId;

    @Size(max= Defs.STR_MAX_LEN)
    private String organizationName;

    @Size(max= Defs.STR_MAX_LEN)
    private String departmentName;

    @Size(max= Defs.STR_MAX_LEN)
    private String description;

    //@Max(Defs.PG_PK_SIZE)
    private Long departmentOwner;
}
