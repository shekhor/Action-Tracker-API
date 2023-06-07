package com.tigerit.soa.entity;

import com.tigerit.soa.loginsecurity.entity.common.EntityCommon;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "roles_actr")
public class RoleEntity extends EntityCommon implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "role_name", nullable = false, length = 50)
        private String roleName;

        @Column(name = "status", nullable = false, length = 15)
        private String status;

        @Column(name = "description", nullable = false, length = 50)
        private String description;

        @Column(name = "priority")
        private Integer priority;

}
/*
--has create update delete and read role
INSERT INTO ROLES_ACTR(ID,ROLE_NAME, STATUS, DESCRIPTION) VALUES(1,'SUPER_ADMIN', 'ACTIVE', 'SUPER ADMIN ROLE');

--has create update and read role
INSERT INTO ROLES_ACTR(ID,ROLE_NAME, STATUS, DESCRIPTION) VALUES(2,'ORG_ADMIN', 'ACTIVE', 'PROJECT OWNER ROLE');//same as super


--has create update and read role
--permission(all) excludes
INSERT INTO ROLES_ACTR(ID,ROLE_NAME, STATUS, DESCRIPTION) VALUES(2,'PROJECT_OWNER', 'ACTIVE', 'PROJECT OWNER ROLE');

--has create and read role
--same as project_owner
INSERT INTO ROLES_ACTR(ID,ROLE_NAME, STATUS, DESCRIPTION) VALUES(3,'PROJECT_MANAGER', 'ACTIVE', 'PROJECT MANAGER ROLE');

--has only read role
--permission, project(create, update,delete, archieve) ,department(create, update,delete, archieve), organization(create, update,delete, archieve) excludes
INSERT INTO ROLES_ACTR(ID,ROLE_NAME, STATUS, DESCRIPTION) VALUES(4,'COMMON_USER', 'ACTIVE', 'COMMON USER  ROLE');

* */

///*
//--Initial roles insert query
//--has create update delete and read role
//INSERT INTO ROLES_ACTR(ROLE_NAME, STATUS, DESCRIPTION) VALUES('SUPER_ADMIN', 'ACTIVE', 'SUPER ADMIN ROLE');
//
//--has create update and read role
//INSERT INTO ROLES_ACTR(ROLE_NAME, STATUS, DESCRIPTION) VALUES('PROJECT_OWNER', 'ACTIVE', 'PROJECT OWNER ROLE');
//
//--has create and read role
//INSERT INTO ROLES_ACTR(ROLE_NAME, STATUS, DESCRIPTION) VALUES('PROJECT_MANAGER', 'ACTIVE', 'PROJECT MANAGER ROLE');
//
//--has only read role
//INSERT INTO ROLES_ACTR(ROLE_NAME, STATUS, DESCRIPTION) VALUES('COMMON_USER', 'ACTIVE', 'COMMON USER  ROLE');
//* */
//
