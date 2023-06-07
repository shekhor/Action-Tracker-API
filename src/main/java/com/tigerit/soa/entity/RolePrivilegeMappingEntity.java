package com.tigerit.soa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/*
Fahim created at 4/8/2020
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_privilege_mapping_actr")
public class RolePrivilegeMappingEntity implements Serializable {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "privilege_id")
    private Long privilegeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePrivilegeMappingEntity that = (RolePrivilegeMappingEntity) o;
        return id == that.id &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(privilegeId, that.privilegeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleId, privilegeId);
    }

}
/*--TEST PERMISSION
--SUPER_ADMIN HAS CREATE, UPDATE, DELETE AND READ PERMISSION ROLES
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (1, 1,1);
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (2, 1,2);
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (3, 1,3);
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (4, 1,4);
--PROJECT_OWNER HAS CREATE, UPDATE AND READ PERMISSION ROLES
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (5, 2,1);
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (6, 2,2);
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (7, 2,4);
--PROJECT_MANAGER  HAS CREATE AND READ PERMISSION
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (8, 3,1);
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (9, 3,4);
--COMMON_USER HAS ONLY READ PERMISSION
INSERT INTO  role_privilege_mapping_actr(ID, ROLE_ID, privilege_id) VALUES (10, 4,4);
*/

//
///*
//--TEST PERMISSION
//--SUPER_ADMIN HAS CREATE, UPDATE, DELETE AND READ PERMISSION ROLES
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (1,1);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (1,2);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (1,3);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (1,4);

//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (1,5);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (1,6);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (1,7);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (1,8);

//--PROJECT_OWNER HAS CREATE, UPDATE AND READ PERMISSION ROLES
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (2,1);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (2,2);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (2,4);
//--PROJECT_MANAGER  HAS CREATE AND READ PERMISSION
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (3,1);
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (3,4);
//--COMMON_USER HAS ONLY READ PERMISSION
//INSERT INTO  role_privilege_mapping(ROLE_ID, privilege_id) VALUES (4,4);
//* */

