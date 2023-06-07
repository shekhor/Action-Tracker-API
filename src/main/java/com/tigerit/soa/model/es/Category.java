package com.tigerit.soa.model.es;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/*
Fahim created at 5/14/2020
*/
@Data
public class Category {
    private String id;
    @NotEmpty(message = "Category can not by null or empty")
    private String categoryName;
    @NotEmpty(message = "Operation must be defined")
    private String operation;
}
