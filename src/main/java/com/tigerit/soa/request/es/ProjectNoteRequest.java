package com.tigerit.soa.request.es;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by DIPU on 5/5/20
 */
@Data
public class ProjectNoteRequest extends Note{

    @NotNull
    @NotEmpty
    private String projectId;
}
