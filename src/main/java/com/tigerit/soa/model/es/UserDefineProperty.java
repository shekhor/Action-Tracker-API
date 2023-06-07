package com.tigerit.soa.model.es;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by DIPU on 5/12/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDefineProperty implements Serializable {
    private String columnNo;
    private String header;
    private String body;
    private String footer;
}
