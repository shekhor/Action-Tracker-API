package com.tigerit.soa.loginsecurity.models.request;

import com.tigerit.soa.loginsecurity.util.core.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest implements Serializable {

    private String username;

    private String password;

    @NotNull(message="User Type should not be null!!")
    private UserType userType;
}