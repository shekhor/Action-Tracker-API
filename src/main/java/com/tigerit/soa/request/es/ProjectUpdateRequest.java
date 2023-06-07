package com.tigerit.soa.request.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by DIPU on 5/17/20
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequest {

    @NotNull
    @NotEmpty
    @Size(min=0, max=255)
    private String projectHeader;
}
