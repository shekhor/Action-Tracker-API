package com.tigerit.soa.service;

import com.tigerit.soa.request.category.CategoryListRequest;
import com.tigerit.soa.request.category.CategoryMappingRequest;
import com.tigerit.soa.response.ServiceResponse;

public interface CategoryService {
    ServiceResponse mapping(CategoryMappingRequest request, String username);

    ServiceResponse list(CategoryListRequest request, String username);
}
