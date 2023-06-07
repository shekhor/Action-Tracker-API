package com.tigerit.soa.loginsecurity.models.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailVerificationModel {
    @NotNull(message = "OTP should not be null")
    @NotEmpty(message = "OTP should not be empty")
    String otp;
}
