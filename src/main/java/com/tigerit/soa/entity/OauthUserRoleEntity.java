package com.tigerit.soa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/*
Fahim created at 4/8/2020
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth_user_role")
public class OauthUserRoleEntity implements Serializable {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name = "priority")
    private Short priority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OauthUserRoleEntity that = (OauthUserRoleEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(roleName, that.roleName) &&
                Objects.equals(status, that.status) &&
                Objects.equals(description, that.description) &&
                Objects.equals(priority, that.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleName, status, description, priority);
    }

}
