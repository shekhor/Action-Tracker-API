package com.tigerit.soa.loginsecurity.models.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tigerit.soa.entity.PrivilegeEntity;
import com.tigerit.soa.loginsecurity.util.core.Status;
import lombok.Data;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ToString
@Valid
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivilegeBean {
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 64)
    private String privilegeName;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 256)
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 64)
    private String privilegeGroupId;


    @NotNull
    @NotEmpty
    @Size(min = 1, max = 512)
    private String apiEndPoint;


    public PrivilegeBean() {
    }

    public PrivilegeBean(PrivilegeEntity privilegeEntity) {
        this.id = privilegeEntity.getId();
        this.privilegeName = privilegeEntity.getPrivilegeName();
        this.description = privilegeEntity.getDescription();
        this.status = privilegeEntity.getStatus();
        this.privilegeGroupId = privilegeEntity.getPrivilegeGroupId();
        this.apiEndPoint = privilegeEntity.getApiEndPoint();
    }

}