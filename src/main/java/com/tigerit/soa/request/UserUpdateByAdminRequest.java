package com.tigerit.soa.request;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigInteger;

@Data
public class UserUpdateByAdminRequest {
    @Column(nullable = false,unique = true)
    private String userId;
    private BigInteger organizationId;
    private BigInteger userRole;
}
