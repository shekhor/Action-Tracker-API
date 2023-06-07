package com.tigerit.soa.entity.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by DIPU on 5/12/20
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class ActionStatusEntity implements Serializable {

    @Id
    private String id;

    private String statusName;
    private String description;
    private String status;
    private String type;
    private String toggledOn;

}
