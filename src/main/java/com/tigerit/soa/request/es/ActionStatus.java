package com.tigerit.soa.request.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;

/**
 * Created by DIPU on 5/14/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionStatus {

    private String id;

    private String statusName;
    private String description;
    private String status;
    private String type;
    private String toggledOn;
}
