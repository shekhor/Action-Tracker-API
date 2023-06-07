package com.tigerit.soa.entity;

import com.tigerit.soa.loginsecurity.entity.common.EntityCommon;
import com.tigerit.soa.loginsecurity.entity.enums.UserCategory;
import com.tigerit.soa.loginsecurity.models.request.UserBean;
import com.tigerit.soa.loginsecurity.util.core.Status;
import com.tigerit.soa.loginsecurity.util.core.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_actr")
public class UserEntity extends EntityCommon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name",nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name",nullable = false, length = 50)
    private String lastName;

    @Column(name = "username",nullable = false, length = 120, unique = true)
    private String username;

    @Column(name = "email",nullable = false, length = 120, unique = true)
    private String email;

    @Column(name = "encrypted_password",nullable = false)
    private String encryptedPassword;

    @Column(name = "user_id",nullable = false,unique = true)
    private String userId;

    @Column(name = "user_role_id", nullable = false)
    private BigInteger userRoleId;

    @Column(name = "organization_id",nullable = false)
    private BigInteger organizationId;

    @Column(name = "domain_name", nullable = false)
    private String domainName;

    @Column(name = "logged_out_at")
    @UpdateTimestamp
    private Timestamp loggedOutAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name = "USER_CATEGORY")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{error.type.null}")
    private UserCategory userCategory;

    @Column(name = "OTP")
    private String otp ;

    @Column(name = "LAST_UPDATED_TIME")
    @UpdateTimestamp
    private Date lastUpdatedTime;

    public UserEntity(UserBean userBean){

        if(userBean.getId()!=null){
            this.id=userBean.getId();
        }
        this.firstName=userBean.getFirstName();
        this.lastName=userBean.getLastName();
        //this.username=userBean.getEmail();
        this.email=userBean.getEmail();
        //this.encryptedPassword=userBean.getEncryptedPassword();
        //this.userId=userBean.getUsername();
        //this.userRoleId = userBean.getUserRoleId();
        //this.organizationId = userBean.getOrganizationId();
        //this.domainName = userBean.getDomainName();
        //this.status=userBean.getStatus();
        this.userType=userBean.getUserType();
    }

}

/*
INSERT INTO public.users_actr(
	id, first_name, last_name, username, email, encrypted_password, user_id, user_role_id, organization_id, domain_name, logged_out_at, status, created_by, create_time, edited_by, edit_time, internal_version)
	VALUES (1, 'Apu1', 'chak', 'user1@gmail.com', 'user1@gmail.com', '12345678', 'Apu1', 1, 1, 'tigerit.com', null, 'ACTIVE', 'user1@gmail.com', '2020-05-10 19:41:22.787147+06', null, null, 0);


	INSERT INTO public.users_actr(
	id, first_name, last_name, username, email, encrypted_password, user_id, user_role_id, organization_id, domain_name, logged_out_at, status, created_by, create_time, edited_by, edit_time, internal_version)
	VALUES (2, 'Apu2', 'chak', 'user2@gmail.com', 'user2@gmail.com', '12345678', 'Apu2', 2, 1, 'tigerit.com', null, 'ACTIVE', 'user2@gmail.com', '2020-05-10 19:41:22.787147+06', null, null, 0);


	INSERT INTO public.users_actr(
	id, first_name, last_name, username, email, encrypted_password, user_id, user_role_id, organization_id, domain_name, logged_out_at, status, created_by, create_time, edited_by, edit_time, internal_version)
	VALUES (3, 'Apu3', 'chak', 'user3@gmail.com', 'user3@gmail.com', '12345678', 'Apu3', 3, 1, 'tigerit.com', null, 'ACTIVE', 'user3@gmail.com', '2020-05-10 19:41:22.787147+06', null, null, 0);


	INSERT INTO public.users_actr(
	id, first_name, last_name, username, email, encrypted_password, user_id, user_role_id, organization_id, domain_name, logged_out_at, status, created_by, create_time, edited_by, edit_time, internal_version)
	VALUES (4, 'Apu4', 'chak', 'user4@gmail.com', 'user4@gmail.com', '12345678', 'Apu4', 4, 1, 'tigerit.com', null, 'ACTIVE', 'user4@gmail.com', '2020-05-10 19:41:22.787147+06', null, null, 0);


    UPDATE USERs_ACTR SET ENCRYPTED_PASSWORD= '$2a$10$PjYTdj.e0eZu0hhSbVqxZeJM9o.d6NO0TxMAATFZggWVokJLuiMLm';
	*/