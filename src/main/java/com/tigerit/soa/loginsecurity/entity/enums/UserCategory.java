package com.tigerit.soa.loginsecurity.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UserCategory {
    SUPER_ADMIN("SUPER_ADMIN"),
    ORGANIZATION_ADMIN("ORGANIZATION_ADMIN"),
    PROJECT_OWNER("PROJECT_OWNER"),
    PROJECT_MANAGER("PROJECT_MANAGER"),
    COMMON_USER("COMMON_USER");

    private String category;

    UserCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}