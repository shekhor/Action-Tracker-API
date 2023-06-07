package com.tigerit.soa.entity;

import com.tigerit.soa.loginsecurity.entity.common.EntityCommon;
import com.tigerit.soa.loginsecurity.models.bean.PrivilegeBean;
import com.tigerit.soa.loginsecurity.util.core.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "privilege_actr")
public class PrivilegeEntity extends EntityCommon implements Serializable {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PRIVILEGE_NAME", nullable = false)
    private String privilegeName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Status status;

    @Column(name = "PRIVILEGE_GROUP_ID")
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 64)
    private String privilegeGroupId;

    @Column(name = "API_END_POINT")
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 512)
    private String apiEndPoint;



    public PrivilegeEntity(PrivilegeBean privilegeBean) {
        this.id = privilegeBean.getId();
        this.privilegeName = privilegeBean.getPrivilegeName();
        this.description = privilegeBean.getDescription();
        this.status = privilegeBean.getStatus();
        this.privilegeGroupId = privilegeBean.getPrivilegeGroupId();
        this.apiEndPoint = privilegeBean.getApiEndPoint();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivilegeEntity that = (PrivilegeEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(privilegeName, that.privilegeName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, privilegeName, description, status);
    }

}

/*Test insert sql:
INSERT INTO PRIVILEGE_ACTR(id,privilege_name, description, status) VALUES(1,'CREATE_DEMO','CREATE DEMO TEST','ACTIVE');
INSERT INTO PRIVILEGE_ACTR(id,privilege_name, description, status) VALUES(2,'UPDATE_DEMO','UPDATE DEMO TEST','ACTIVE');
INSERT INTO PRIVILEGE_ACTR(id,privilege_name, description, status) VALUES(3,'DELETE_DEMO','DELETE DEMO TEST','ACTIVE');
INSERT INTO PRIVILEGE_ACTR(id,privilege_name, description, status) VALUES(4,'READ_DEMO','READ DEMO TEST','ACTIVE');
*/

///*
//Test insert sql:
//INSERT INTO PRIVILEGE(privilege_name, description, status) VALUES('CREATE_DEMO','CREATE DEMO TEST','ACTIVE');
//INSERT INTO PRIVILEGE(privilege_name, description, status) VALUES('UPDATE_DEMO','UPDATE DEMO TEST','ACTIVE');
//INSERT INTO PRIVILEGE(privilege_name, description, status) VALUES('DELETE_DEMO','DELETE DEMO TEST','ACTIVE');
//INSERT INTO PRIVILEGE(privilege_name, description, status) VALUES('READ_DEMO','READ DEMO TEST','ACTIVE');

//INSERT INTO PRIVILEGE(privilege_name, description, status) VALUES('USER_DETAILS_BY_ID','USER DETAILS BY ID','ACTIVE');
//INSERT INTO PRIVILEGE(privilege_name, description, status) VALUES('USER_LIST','USER LIST','ACTIVE');
//INSERT INTO PRIVILEGE(privilege_name, description, status) VALUES('USER_CREATE','USER CREATE','ACTIVE');
//INSERT INTO PRIVILEGE(privilege_name, description, status) VALUES('USER_UPDATE','USER UPDATE','ACTIVE');

//* */
