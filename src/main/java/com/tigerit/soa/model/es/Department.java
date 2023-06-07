package com.tigerit.soa.model.es;

import com.tigerit.soa.entity.es.CommonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by DIPU on 4/9/20
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department extends CommonProperty implements Serializable {

    private String id;

    private String organizationId;

    private String organizationName;

    private String departmentName;

    private String description;

    private Long departmentOwner;

    private String status;
}
