package com.tigerit.soa.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest implements Serializable {
    @Column(nullable = false,unique = true)
    private String userId;
    private String firstName;
    private String lastName;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
