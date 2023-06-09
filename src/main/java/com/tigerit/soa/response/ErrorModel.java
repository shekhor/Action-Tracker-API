package com.tigerit.soa.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
Fahim created at 4/9/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorModel implements Serializable {

    private String message;
    private String field;
    private String description;
}
