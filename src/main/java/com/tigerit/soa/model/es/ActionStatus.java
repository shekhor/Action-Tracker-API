package com.tigerit.soa.model.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by DIPU on 5/14/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionStatus implements Serializable {
    private String id;
    private String statusName;
    private String description;
    private String status;
    private String type;
    private String toggledOn;

}
