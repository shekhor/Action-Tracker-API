package com.tigerit.soa.model.es;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by DIPU on 5/5/20
 */
@Data
public class ProjectNote implements Serializable {
    private String id;
    private String note;
    private String domainName;
    private String accessLevel;
    private String createdBy;
    private Date createTime;
    private Date updateTime;
    private String status;
    private String projectId;
}
