package com.tigerit.soa.request.category;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/*
Fahim created at 5/14/2020
*/
@Data
public class CategoryListRequest {
    @NotEmpty(message = "Project Id can not by null or empty")
    private String projectId;
}
