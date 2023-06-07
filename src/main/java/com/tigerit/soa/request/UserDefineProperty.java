package com.tigerit.soa.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
Fahim created at 5/5/2020
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDefineProperty {
    private String columnNo;
    private String header;
    private String body;
    private String footer;
}
