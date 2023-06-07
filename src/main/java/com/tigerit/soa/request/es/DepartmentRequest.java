package com.tigerit.soa.request.es;

import com.tigerit.soa.util.Defs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

/**
 * Created by DIPU on 4/9/20
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequest {

    @Size(max = Defs.STR_MAX_LEN)
    private String id;
    @NotNull()
    @NotEmpty
    @Size(min=Defs.STR_MIN_LEN, max = Defs.STR_MAX_LEN)
    private String organizationId;

    @NotNull()
    @NotEmpty
    @Size(min=Defs.STR_MIN_LEN, max = Defs.STR_MAX_LEN)
    private String organizationName;

    @NotNull
    @NotEmpty
    @Size(min=Defs.STR_MIN_LEN, max = Defs.STR_MAX_LEN)
    private String departmentName;

    @NotNull()
    @NotEmpty
    @Size(min=Defs.STR_MIN_LEN, max = Defs.STR_MAX_LEN)
    private String description;

    @NotNull
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    private Long departmentOwner;

    @Size(max = Defs.STR_MAX_LEN)
    private String status;

}
