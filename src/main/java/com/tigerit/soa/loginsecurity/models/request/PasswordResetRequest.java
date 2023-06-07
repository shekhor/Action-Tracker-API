package com.tigerit.soa.loginsecurity.models.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.tigerit.soa.loginsecurity.util.ValidationUtils.MAX_PASSWORD_SIZE;
import static com.tigerit.soa.loginsecurity.util.ValidationUtils.MIN_PASSWORD_SIZE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PasswordResetRequest {
    @NotEmpty(message = "{error.password.null}")
    @NotNull(message = "{error.password.null}")
    @Size(max = MAX_PASSWORD_SIZE, message = "{error.password.max.size}")
    @Size(min = MIN_PASSWORD_SIZE, message = "{error.password.min.size}")
    private String oldPassword;


    @NotEmpty(message = "{error.password.null}")
    @NotNull(message = "{error.password.null}")
    @Size(max = MAX_PASSWORD_SIZE, message = "{error.password.max.size}")
    @Size(min = MIN_PASSWORD_SIZE, message = "{error.password.min.size}")
    private String newPassword;

    @NotEmpty(message = "{error.password.null}")
    @NotNull(message = "{error.confirm.password.null}")
    @Size(max = MAX_PASSWORD_SIZE, message = "{error.password.max.size}")
    private String confirmPassword;
}