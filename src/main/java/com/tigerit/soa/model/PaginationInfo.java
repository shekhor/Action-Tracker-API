package com.tigerit.soa.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * Created by DIPU on 6/3/20
 */
@Data
public class PaginationInfo implements Serializable {
    @Min(value=0, message = "pageNo must be at least 0")
    private int pageNo;
    @Min(value=1, message = "pageSize must be at least 1")
    @Max(value = 150, message = "PageSize shouldn't be more that 150")
    private int pageSize;
    private String sortBy;
}
