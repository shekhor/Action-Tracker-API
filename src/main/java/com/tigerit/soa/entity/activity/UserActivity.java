package com.tigerit.soa.entity.activity;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.loginsecurity.entity.common.ActivityAction;
import com.tigerit.soa.loginsecurity.entity.common.ActivityCommon;
import com.tigerit.soa.loginsecurity.entity.common.EntityCommon;
import com.tigerit.soa.loginsecurity.entity.enums.UserCategory;
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
@Table(name = "ACTIVITY_USERS_ACTR")
public class UserActivity extends ActivityCommon{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "username", nullable = false, length = 120, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 120, unique = true)
    private String email;

    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    @Column(name = "user_id", nullable = false,unique = true)
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

    public UserActivity(UserEntity user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.encryptedPassword = user.getEncryptedPassword();
        this.userId = user.getUserId();
        this.userRoleId = user.getUserRoleId();
        this.organizationId = user.getOrganizationId();
        this.domainName = user.getDomainName();
        this.status = user.getStatus();
        this.userType = user.getUserType();
        this.userCategory = user.getUserCategory();
        this.otp = user.getOtp();

        this.createdBy = user.getCreatedBy();
        this.createTime = user.getCreateTime();
        if(user.getEditedBy()!=null){
            this.editedBy = user.getEditedBy();
        }
        if(user.getEditTime()!=null){
            this.editTime = user.getEditTime();
        }
        if(user.getVersion()!=null){
            this.version = user.getVersion();
        }
    }

    public UserActivity(UserEntity user, String userName, ActivityAction activityAction, Date activityTime) {
        this(user);

        this.activityUser = userName;
        this.activityAction = activityAction.getAction();
        this.activityTime = activityTime;
    }
}
