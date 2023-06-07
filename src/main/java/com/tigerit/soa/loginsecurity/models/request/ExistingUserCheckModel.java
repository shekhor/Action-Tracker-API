package com.tigerit.soa.loginsecurity.models.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExistingUserCheckModel {

    @NotNull(message = "Email should not be null")
    @NotEmpty(message = "Email should not be empty")
    String email;
}
