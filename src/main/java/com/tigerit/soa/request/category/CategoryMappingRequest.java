package com.tigerit.soa.request.category;

import com.tigerit.soa.model.es.Category;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/*
Fahim created at 5/14/2020
*/
@Data
public class CategoryMappingRequest {
    @NotEmpty(message = "Project Id can not by null or empty")
    private String projectId;
    List<Category> categoryList;
}
