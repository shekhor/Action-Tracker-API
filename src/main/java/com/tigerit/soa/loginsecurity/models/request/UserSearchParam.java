package com.tigerit.soa.loginsecurity.models.request;

import com.tigerit.soa.loginsecurity.entity.enums.UserCategory;
import com.tigerit.soa.loginsecurity.models.PaginationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchParam implements Serializable {
    private PaginationParam paginationParam;
    //private UserCategory userCategory;
    private Long organizationId;
}