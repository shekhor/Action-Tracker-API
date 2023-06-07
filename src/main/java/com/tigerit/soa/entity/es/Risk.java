package com.tigerit.soa.entity.es;

import com.tigerit.soa.request.UserDefineProperty;
import lombok.Data;

/*
Fahim created at 5/5/2020
*/
@Data
public class Risk {
    private String id;
    private String projectId;
    private UserDefineProperty column1;
    private UserDefineProperty column2;
    private UserDefineProperty column3;
    private UserDefineProperty column4;
}
