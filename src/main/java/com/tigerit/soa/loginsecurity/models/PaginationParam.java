package com.tigerit.soa.loginsecurity.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@Getter
public class PaginationParam {
    private int pageNo;
    private int length;
    private String sortDirection;
    private String sortProperties;
    private String searchParam;

    public String getSearchParamTrimmed() {
        return StringUtils.trimToEmpty(searchParam);
    }

    public boolean isSearchParamPresent() {
        return StringUtils.isNotEmpty(searchParam);
    }
}