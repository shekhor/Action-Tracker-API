package com.tigerit.soa.entity;

import com.tigerit.soa.loginsecurity.entity.common.EntityCommon;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "PRIVILEGE_GROUP_ACTR")
public class PrivilegeGroup extends EntityCommon {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column( name = "ID" )
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "METHOD_ACCESS_GROUP_SEQ")
    //@SequenceGenerator(name = "PRIVILEGE_GROUP_ACTR_SEQ", sequenceName = "PRIVILEGE_GROUP_ACTR_SEQ", allocationSize = 2)
    private Long id;

    @Column( name = "PRIVILEGE_GROUP_NAME" )
    @NotNull
    @NotEmpty
    private String groupName;
}

/*
//INSERT INTO PRIVILEGE_GROUP_ACTR(PRIVILEGE_GROUP_NAME) VALUES('USER_MANAGEMENT');
*/