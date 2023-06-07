package com.tigerit.soa.loginsecurity.service;

//import com.tigerit.nid2.core.model.api.UserBean;
//import com.tigerit.nid2.partnerservice.entity.db.User;
//import com.tigerit.nid2.partnerservice.entity.enums.UserCategory;
//import com.tigerit.nid2.partnerservice.model.PaginationParam;
//import com.tigerit.nid2.partnerservice.model.request.PasswordResetRequest;
//import com.tigerit.nid2.partnerservice.model.request.PasswordUpdateRequest;
//import org.apache.commons.lang3.tuple.Pair;

import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.loginsecurity.entity.enums.UserCategory;
import com.tigerit.soa.loginsecurity.models.PaginationParam;
import com.tigerit.soa.loginsecurity.models.request.PasswordResetRequest;
import com.tigerit.soa.loginsecurity.models.request.PasswordUpdateRequest;
import com.tigerit.soa.loginsecurity.models.request.UserBean;
import com.tigerit.soa.loginsecurity.models.request.UserBeanForUpdate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;


public interface UserService {

    UserBean getUserBean(UserEntity user);

    UserBean findById(Long id);

    UserBean findByUserName(String username);

    UserBean findByEmail(String email);

    UserBean createUser(UserBean bean, String username, UserCategory userCategory);

    UserBean updateUser(UserBeanForUpdate bean, Long id, String username);

    UserBean activateUser(Long id, String otp);

    Pair<List<UserBean>, Long> getAll(PaginationParam param, /*UserCategory category,*/ Long organizationId);

    boolean deactivateUser(String username, Long subUserId);

    List<String> getListOfRoles(String username);

    boolean updatePassword(String username, PasswordUpdateRequest passUpdateRequest);
    boolean resetPassword(PasswordResetRequest resetRequest, String adminUserName, Long userId);
    boolean changePassword(PasswordResetRequest resetRequest, String adminUserName, Long userId);
    boolean forgetPassword(PasswordResetRequest resetRequest, String adminUserName, Long userId);

    boolean expirePassword(Long userId, String adminUserName);

}
