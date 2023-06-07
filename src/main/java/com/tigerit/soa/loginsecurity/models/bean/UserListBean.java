package com.tigerit.soa.loginsecurity.models.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tigerit.soa.loginsecurity.models.request.UserBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserListBean implements Serializable {
    private List<UserBean> userBeanList;
    private Long userCount;
}