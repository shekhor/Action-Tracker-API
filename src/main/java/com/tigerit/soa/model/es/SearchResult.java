package com.tigerit.soa.model.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by DIPU on 4/23/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult<T> {
    private long totalHit;
    private List<T> resultList;
}
