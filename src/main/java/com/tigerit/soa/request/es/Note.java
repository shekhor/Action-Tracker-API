package com.tigerit.soa.request.es;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by DIPU on 5/5/20
 */
@Data
public class Note {

    //TODO: need to add more specific validation later
    private String id;
    @NotNull
    @NotEmpty
    private String note;

    @NotNull
    @NotEmpty
    private String domainName;
    @NotNull
    @NotEmpty
    private String accessLevel;

    @NotNull
    @NotEmpty
    private String created_by;

    private Date createTime;

    private Date updateTime;
    @NotNull
    @NotEmpty
    private String status;

}
