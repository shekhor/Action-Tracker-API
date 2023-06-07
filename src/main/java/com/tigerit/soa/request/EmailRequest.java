package com.tigerit.soa.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by DIPU on 6/4/20
 */

@Data
public class EmailRequest {
    private String from;
    @Email
    private String to;
    private String subject;
    @NotNull
    @NotEmpty
    private String body;

}
