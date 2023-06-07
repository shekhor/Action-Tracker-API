package com.tigerit.soa.controller;

import com.tigerit.soa.loginsecurity.component.exception.ActrServiceException;
import com.tigerit.soa.loginsecurity.component.exception.PasswordMismatchException;
import com.tigerit.soa.loginsecurity.entity.enums.UserCategory;
import com.tigerit.soa.loginsecurity.models.PaginationParam;
import com.tigerit.soa.loginsecurity.models.bean.UserListBean;
import com.tigerit.soa.loginsecurity.models.request.*;
import com.tigerit.soa.loginsecurity.models.response.RestApiResponse;
import com.tigerit.soa.loginsecurity.service.UserService;
import com.tigerit.soa.loginsecurity.util.SessionKey;
import com.tigerit.soa.loginsecurity.util.Util;
import com.tigerit.soa.loginsecurity.util.core.Status;
import com.tigerit.soa.loginsecurity.util.core.UserType;
import com.tigerit.soa.loginsecurity.validator.UserValidator;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

import static com.tigerit.soa.loginsecurity.util.ResponseMessages.INTERNAL_SERVER_ERROR;


@RestController
@RequestMapping(path = "/rest/user")
/*@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
        @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
})*/
//@SecurityRequirement(name = "bearer-auth")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    private UserValidator userValidator;

    private MessageSource messageSource;
    private static final String PASSWORD="ACTR";

    @Autowired
    PasswordEncoder encoder;

    public UserController(UserService userService,
                          UserValidator validator,
                          MessageSource messageSource) {
        this.userService = userService;
        this.userValidator = validator;
        this.messageSource = messageSource;
    }

    @InitBinder("userBean")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(userValidator);
    }

    @RequestMapping(path = "/details/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('USER_DETAILS_BY_ID')")
    //@Operation(summary = "Get User Details By ID", description = "USER_DETAILS_BY_ID Privilege Is Required")
    public ResponseEntity<RestApiResponse<UserBean>> getUserDetailsById(@PathVariable(name = "id") Long userId,
                                                                        HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        logger.debug("Get User Details request from: {} with id: {}", userDetails.getUsername(), userId);
        UserBean userBean = userService.findById(userId);
        RestApiResponse<UserBean> restApiResponse;
        if (userBean != null) {
            restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, userBean);
        } else {
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.NOT_FOUND, "id",
                    "No User Exists!!");
        }
        return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
    }

    @RequestMapping(path = "/list", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('USER_LIST')")
    //@Operation(summary = "Fetch Paginated User List", description = "USER_LIST Privilege Is Required")
    public ResponseEntity<RestApiResponse<UserListBean>> getUserList(@RequestBody UserSearchParam userSearchParam,
                                                                     HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        logger.debug("Fetch Paginated User List request from: {} with UserSearchParam: {}",
                userDetails.getUsername(), userSearchParam.toString());
        PaginationParam paginationParam = userSearchParam.getPaginationParam();
        //UserCategory userCategory = userSearchParam.getUserCategory();
        Pair<List<UserBean>, Long> users = userService.getAll(paginationParam/*, userCategory*/, userSearchParam.getOrganizationId());
        UserListBean userListBean = new UserListBean(users.getLeft(), users.getRight());
        RestApiResponse<UserListBean> restApiResponse
                = Util.buildSuccessRestResponse(HttpStatus.OK, userListBean);
        return ResponseEntity.status(HttpStatus.OK).body(restApiResponse);
    }


    @RequestMapping(path = "/create", method = RequestMethod.POST)
    //@PreAuthorize("hasAnyRole('USER_CREATE')")
    //@Operation(summary = "Create New User", description = "USER_CREATE Privilege Is Required")
    public ResponseEntity<RestApiResponse<UserBean>> createUser(@Valid @RequestBody UserBean userBean,
                                                                HttpServletRequest request, Locale locale,
                                                                BindingResult bindingResult) {
        if(userBean==null){
            return null;
        }

        if(!StringUtils.isEmpty(userBean.getUserType()) && userBean.getUserType().equals(UserType.SSO)){
            userBean.setEncryptedPassword(userBean.getEncryptedPassword()+PASSWORD);
            userBean.setStatus(Status.ACTIVE);
        }else{
            userBean.setStatus(Status.INACTIVE);
        }
        //UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        //logger.debug("Create New User request from: {} with UserBean: {}", userDetails.getUsername(), userBean.toString());
        RestApiResponse<UserBean> restApiResponse;
        try {

            String validationResult = userValidator.validatePassword(userBean, locale);
            if (validationResult != null && userBean.getUserType()!=UserType.SSO) {
                restApiResponse = Util.buildErrorRestResponse(HttpStatus.BAD_REQUEST, "password", validationResult);
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            }
            //UserBean user = userService.createUser(userBean, userDetails.getUsername(), UserCategory.COMMON_USER);
            userBean.setUsername(userBean.getEmail());
            UserBean user = userService.createUser(userBean, userBean.getUsername(), UserCategory.COMMON_USER);
            restApiResponse = Util.buildSuccessRestResponse(HttpStatus.CREATED, user);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        } catch (Exception ex) {
            logger.debug("Error While Creating User ", ex);
            if (ex instanceof ActrServiceException) {
                ActrServiceException exception = (ActrServiceException) ex;
                throw new ActrServiceException(exception.getHttpStatus(), exception.getField(),
                        messageSource.getMessage(exception.getMessage(), null, locale));
            }
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        }
    }


    @RequestMapping(path = "/update/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('USER_UPDATE')")
    //@Operation(summary = "Update Existing User By Id", description = "USER_UPDATE Privilege Is Required")
    public ResponseEntity<RestApiResponse<UserBean>> updateUser(@PathVariable(name = "id") Long userId,
                                                                @Valid @RequestBody UserBeanForUpdate userBeanForUpdate,
                                                                HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        logger.debug("Update Existing User request from: {} with id: {} and UserBean : {}", userDetails.getUsername(), userId, userBeanForUpdate.toString());
        UserBean user = userService.updateUser(userBeanForUpdate, userId, userDetails.getUsername());
        RestApiResponse<UserBean> restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, user);
        return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
    }

    @RequestMapping(path = "/activate/{id}", method = RequestMethod.PUT)
    //@PreAuthorize("hasAnyRole('USER_ACTIVATE')")
    //@Operation(summary = "Activate Existing User By Id", description = "USER_ACTIVATE Privilege Is Required")
    public ResponseEntity<RestApiResponse<UserBean>> activateUser(@PathVariable(name = "id") Long userId,
                                                                  @Valid @RequestBody EmailVerificationModel emailVerificationModel, Locale locale,
                                                                  HttpServletRequest request) {
        //UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        logger.debug("Activate Existing User request from id: {} ", userId);
        RestApiResponse<UserBean> restApiResponse;
        try{
            logger.debug("Activate request for otp: {} ", emailVerificationModel.getOtp());
            UserBean user = userService.activateUser( userId, emailVerificationModel.getOtp());
            if(user==null){
                restApiResponse = Util.buildErrorRestResponse(HttpStatus.BAD_REQUEST, "otp", "otp is invalid");
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            }
            restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, user);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        } catch (Exception ex) {
            logger.debug("Error While Activating User ", ex);
            if (ex instanceof ActrServiceException) {
                ActrServiceException exception = (ActrServiceException) ex;
                throw new ActrServiceException(exception.getHttpStatus(), exception.getField(),
                        messageSource.getMessage(exception.getMessage(), null, locale));
            }
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        }
    }
    @RequestMapping(path = "/check-user-by-email/", method = RequestMethod.POST)
    //@PreAuthorize("hasAnyRole('USER_ACTIVATE')")
    //@Operation(summary = "Activate Existing User By Id", description = "USER_ACTIVATE Privilege Is Required")
    public ResponseEntity<RestApiResponse<Boolean>> checkExistUserByEmail(@Valid @RequestBody ExistingUserCheckModel existingUserCheckModel, Locale locale,
                                                                     HttpServletRequest request) {
        //UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        logger.debug("Check Existing User request by email: {} ", existingUserCheckModel.getEmail());
        RestApiResponse<Boolean> restApiResponse;
        try{
            UserBean user = userService.findByEmail(existingUserCheckModel.getEmail());
            if(user==null){
                restApiResponse = Util.buildErrorRestResponse(HttpStatus.BAD_REQUEST, "user", "user not found");
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            }
            restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, Boolean.TRUE);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        } catch (Exception ex) {
            logger.debug("Error While Activating User ", ex);
            if (ex instanceof ActrServiceException) {
                ActrServiceException exception = (ActrServiceException) ex;
                throw new ActrServiceException(exception.getHttpStatus(), exception.getField(),
                        messageSource.getMessage(exception.getMessage(), null, locale));
            }
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        }
    }

    /*@RequestMapping(path = "/update-password/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('USER_PASSWORD_UPDATE')")
    //@Operation(summary = "Reset User Password", description = "USER_PASSWORD_RESET Privilege Is Required")
    public ResponseEntity<RestApiResponse<String>> updatePassword(@PathVariable(name = "id") Long userId,
                                                                 @Valid @RequestBody PasswordResetRequest resetRequest,
                                                                 HttpServletRequest request, Locale locale) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        logger.debug("Reset Common User Password request from: {} with id: {}", userDetails.getUsername(), userId);
        RestApiResponse<String> restApiResponse;
        try {
            String username = userDetails.getUsername();

            boolean resetResult = userService.resetPassword(resetRequest, username, userId);
            if (resetResult) {
                String successMsg = "Password Reset Successful";
                restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, successMsg);
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            } else {
                String errorMsg = "Password Reset Failed";
                restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, errorMsg);
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            }
        } catch (PasswordMismatchException ex) {
            logger.debug("PasswordMismatchException found, message: {}", ex.getMessage());
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(ex.getField(), null, locale),
                    messageSource.getMessage(ex.getMessage(), null, locale));
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        } catch (Exception ex) {
            logger.debug("Error While Creating User ", ex);
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        }
    }
    @RequestMapping(path = "/forget-password/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('USER_PASSWORD_FORGET')")
    //@Operation(summary = "Reset User Password", description = "USER_PASSWORD_RESET Privilege Is Required")
    public ResponseEntity<RestApiResponse<String>> forgetPassword(@PathVariable(name = "id") Long userId,
                                                                  @Valid @RequestBody PasswordResetRequest resetRequest,
                                                                  HttpServletRequest request, Locale locale) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        logger.debug("Reset Common User Password request from: {} with id: {}", userDetails.getUsername(), userId);
        RestApiResponse<String> restApiResponse;
        try {
            String username = userDetails.getUsername();

            boolean resetResult = userService.resetPassword(resetRequest, username, userId);
            if (resetResult) {
                String successMsg = "Password Reset Successful";
                restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, successMsg);
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            } else {
                String errorMsg = "Password Reset Failed";
                restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, errorMsg);
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            }
        } catch (PasswordMismatchException ex) {
            logger.debug("PasswordMismatchException found, message: {}", ex.getMessage());
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(ex.getField(), null, locale),
                    messageSource.getMessage(ex.getMessage(), null, locale));
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        } catch (Exception ex) {
            logger.debug("Error While Creating User ", ex);
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        }
    }
    @RequestMapping(path = "/reset-password/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('USER_PASSWORD_UPDATE')")
    //@Operation(summary = "Reset User Password", description = "USER_PASSWORD_RESET Privilege Is Required")
    public ResponseEntity<RestApiResponse<String>> resetPassword(@PathVariable(name = "id") Long userId,
                                                                  @Valid @RequestBody PasswordResetRequest resetRequest,
                                                                  HttpServletRequest request, Locale locale) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
        logger.debug("Reset Common User Password request from: {} with id: {}", userDetails.getUsername(), userId);
        RestApiResponse<String> restApiResponse;
        try {
            String username = userDetails.getUsername();

            boolean resetResult = userService.resetPassword(resetRequest, username, userId);
            if (resetResult) {
                String successMsg = "Password Reset Successful";
                restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, successMsg);
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            } else {
                String errorMsg = "Password Reset Failed";
                restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, errorMsg);
                return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
            }
        } catch (PasswordMismatchException ex) {
            logger.debug("PasswordMismatchException found, message: {}", ex.getMessage());
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(ex.getField(), null, locale),
                    messageSource.getMessage(ex.getMessage(), null, locale));
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        } catch (Exception ex) {
            logger.debug("Error While Creating User ", ex);
            restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
        }
    }*/

//
//
//    @RequestMapping(path = "/expire-password/{id}", method = RequestMethod.GET)
//    @PreAuthorize("hasAnyRole('USER_PASSWORD_EXPIRE')")
//    //@Operation(summary = "Expire User Password", description = "USER_PASSWORD_RESET Privilege Is Required")
//    public ResponseEntity<RestApiResponse<String>> expirePassword(@PathVariable(name = "id") Long userId,
//                                                                  HttpServletRequest request) {
//        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
//        logger.debug("Reset Sub User Password request from: {} with id: {}", userDetails.getUsername(), userId);
//        RestApiResponse<String> restApiResponse;
//        String username = userDetails.getUsername();
//
//        boolean resetResult = userService.expirePassword(userId, username);
//        if (resetResult) {
//            String successMsg = "Password Expire Successful";
//            restApiResponse = Util.buildSuccessRestResponse(HttpStatus.OK, successMsg);
//            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
//        } else {
//            String errorMsg = "Password Expire Failed";
//            restApiResponse = Util.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, errorMsg);
//            return ResponseEntity.status(restApiResponse.getStatus()).body(restApiResponse);
//        }
//
//    }
}
