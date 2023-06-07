package com.tigerit.soa.entity;


import lombok.*;

import javax.persistence.*;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PRIVILEGE_GROUP_PRIVILEGE_MAPPING_ACTR")
public class PrivilegeGroupPrivilegeMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "ID" )
    private Long id;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "PRIVILEGE_ID")
    private Long privilegeId;
}
