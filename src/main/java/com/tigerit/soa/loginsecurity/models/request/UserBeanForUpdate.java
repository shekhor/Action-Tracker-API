package com.tigerit.soa.loginsecurity.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tigerit.soa.loginsecurity.entity.enums.UserCategory;
import com.tigerit.soa.loginsecurity.util.core.Status;
import com.tigerit.soa.loginsecurity.util.core.UserType;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;

@Data
public class UserBeanForUpdate {

    private Long id;

    @NotNull(message = "First Name can not be null")
    @Size(min = 1,max=60, message = "First name must not be less than 1 and less than 60 characters")
    private String firstName;

    @NotNull(message = "Last Name can not be null")
    @Size(min = 1, max=60, message = "Last name must not be less than 1 and less than 60 characters")
    private String lastName;

    //@NotNull(message = "username cannot be null")
    @JsonIgnore
    private String username;

    //@NotNull(message = "email can not be null")
    //@Email
    private String email;

    @JsonIgnore
    //@NotNull(message = "password can not be null")
    //@Size(min = 8,max = 40,message = "password must be equal or grater than 8 character and less then 40 characters")
    private String encryptedPassword;

    @JsonIgnore
    //@NotNull(message = "confirm password can not be null")
    //@Size(min = 8,max = 40,message = "confirm password must be equal or grater than 8 character and less then 40 characters")
    private String confirmEncryptedPassword;

    @JsonIgnore
    private String userId;


    @JsonIgnore
    private BigInteger userRoleId;

    @JsonIgnore
    private BigInteger organizationId;

    //@NotNull(message = "Domain name can not be null")
    @JsonIgnore
    private String domainName;

    @JsonIgnore
    private Timestamp loggedOutAt;

    //@NotNull(message = "Status can not be null")
    //@JsonIgnore
    private Status status;

    @NotNull(message = "UserType can not be null")
    private UserType userType;

    @JsonIgnore
    private UserCategory userCategory;



}
