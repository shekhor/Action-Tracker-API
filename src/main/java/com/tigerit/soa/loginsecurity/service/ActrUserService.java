package com.tigerit.soa.loginsecurity.service;


import com.tigerit.soa.entity.UserEntity;
import com.tigerit.soa.entity.activity.UserActivity;
import com.tigerit.soa.loginsecurity.auth.exception.UserNotFoundException;
import com.tigerit.soa.loginsecurity.component.exception.ActrServiceException;
import com.tigerit.soa.loginsecurity.component.exception.PasswordMismatchException;
import com.tigerit.soa.loginsecurity.entity.common.ActivityAction;
import com.tigerit.soa.loginsecurity.entity.enums.UserCategory;
import com.tigerit.soa.loginsecurity.models.PaginationParam;
import com.tigerit.soa.loginsecurity.models.request.PasswordResetRequest;
import com.tigerit.soa.loginsecurity.models.request.PasswordUpdateRequest;
import com.tigerit.soa.loginsecurity.models.request.UserBean;
import com.tigerit.soa.loginsecurity.models.request.UserBeanForUpdate;
import com.tigerit.soa.loginsecurity.repository.ActivityUserRepository;
import com.tigerit.soa.loginsecurity.repository.UserRepository;
import com.tigerit.soa.loginsecurity.util.RegistrationDef;
import com.tigerit.soa.loginsecurity.util.Util;
import com.tigerit.soa.loginsecurity.util.core.Status;
import com.tigerit.soa.loginsecurity.util.core.UserType;
import com.tigerit.soa.request.OrganizationCreateRequest;
import com.tigerit.soa.response.OrganizationResponse;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.OrganizationService;
import com.tigerit.soa.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class ActrUserService implements UserService {

    private static final BigInteger userDefaultRoleId = BigInteger.valueOf(1);

    private Logger logger = LoggerFactory.getLogger(ActrUserService.class);

    private final PasswordEncoder encoder;

    private final UserRepository userRepository;

    private final ActivityUserRepository activityUserRepository;

    @Autowired
    private EmailService emailService;

    //private final MethodGroupRepository groupRepository;

    //private final UsersGroupRepository usersGroupRepository;

    //private final GroupService groupService;

    //private final UserExtraInfoRepository userExtraInfoRepository;

    //private final RegionRepository regionRepository;

    //private final DistrictRepository districtRepository;

    //private final UpozilaRepository upozilaRepository;

    //private final ThanaEcIdRepository thanaEcIdRepository;

    //private final SMSSender smsSender;

    private final MessageSource messageSource;

    //import organization service
    @Autowired
    OrganizationService organizationService;


    public String getOrganizationId(String domainName, String username){
        ServiceResponse response;
        OrganizationCreateRequest request = new OrganizationCreateRequest();

        //domain name will be substring after @ ..
        //example: abc@gamil.com -> domain name : gmail.com
        //example: abc@tigerit.com -> domain name : tigerit.com
        //request.setDomainName("gmail.com");
        request.setDomainName(domainName);

        //owner name will be registration username
        //request.setOrganizationOwner("username");
        request.setOrganizationOwner(username);

        //request object and username of registration username
        //response = organizationService.createOrganization(request, "username");
        try{
            response = organizationService.createOrganization(request, username);
        }catch (Exception e){
            logger.debug("exception occur while getting organization id from ES by using username: {} ", username);
            throw new ActrServiceException(HttpStatus.FAILED_DEPENDENCY, "organization", "error.organization.id.not.found");
        }

        //in response service will provide the organization id and that needs to be saved in user entity for future purpose
        OrganizationResponse organizationResponse = (OrganizationResponse) response.getBody();
        logger.debug("organization id: "+organizationResponse.getId());
        return organizationResponse.getId();
    }


    @Override
    public UserBean getUserBean(UserEntity user) {
        UserBean userBean = new UserBean();
        userBean.setId(user.getId());
        userBean.setFirstName(user.getFirstName());
        userBean.setLastName(user.getLastName());
        userBean.setUsername(user.getUsername());
        userBean.setEmail(user.getEmail());
        //userBean.setEncryptedPassword(user.getEncryptedPassword());
        userBean.setUserId(user.getUserId());
        userBean.setUserRoleId(user.getUserRoleId());
        userBean.setOrganizationId(user.getOrganizationId());
        userBean.setDomainName(user.getDomainName());
        userBean.setStatus(user.getStatus());
        userBean.setUserType(user.getUserType());
        userBean.setUserCategory(user.getUserCategory());
        userBean.setOtp(user.getOtp());
        return userBean;
    }


    @Override
    public UserBean findById(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(()
                -> new ActrServiceException(HttpStatus.NOT_FOUND, "id", "User not found with this ID: " + id));
        UserBean userBean = getUserBean(user);
     return userBean;
    }

    @Override
    public UserBean createUser(UserBean userBean, String username, UserCategory userCategory) {

        UserEntity user = new UserEntity(userBean);

        this.checkUserExistenceByUsernameAndEmail(userBean);

        String domainName ="";
        String[] emailSplit=userBean.getEmail().split("@");
        if(!(emailSplit==null || emailSplit.length<0)){
            domainName = emailSplit[1];
        }
        user.setUsername(userBean.getEmail());
        user.setEncryptedPassword(encoder.encode(userBean.getEncryptedPassword()));
        user.setUserId(emailSplit[0]);
        user.setUserRoleId(userDefaultRoleId);
        user.setOrganizationId(new BigInteger(getOrganizationId(domainName, username)));
        user.setDomainName(domainName);
        user.setStatus(userBean.getStatus());
        user.setUserType(userBean.getUserType());
        user.setUserCategory(userCategory);
        user.setCreatedBy(username);
        user.setLastUpdatedTime(new Date());

        String token = RandomStringUtils.randomAlphanumeric(16);
        String base64encodedToken="";
        try{
            base64encodedToken = Base64.getEncoder().encodeToString(token.getBytes("utf-8"));
        }catch(UnsupportedEncodingException e) {
            logger.debug("Error :" + e.getMessage());
            throw new ActrServiceException(HttpStatus.BAD_REQUEST, "token", "error.token.encode.exception");
        }


        user.setOtp(token);
        UserEntity userRes = userRepository.saveAndFlush(user);
        UserBean resUserBean= getUserBean(userRes);

        String base64encodedId="";
        try{
            base64encodedId = Base64.getEncoder().encodeToString(resUserBean.getId().toString().getBytes("utf-8"));
        }catch(UnsupportedEncodingException e) {
            logger.debug("Error :" + e.getMessage());
            throw new ActrServiceException(HttpStatus.BAD_REQUEST, "id", "error.id.encode.exception");
        }

        if(user.getUserType().equals(UserType.COMMON)){
            try {

                String BODY=RegistrationDef.EMAIL_VERIFICATION_URL+"?id="+base64encodedId+"&token="+base64encodedToken;
                emailService.sendSimpleMail(userBean.getEmail(), RegistrationDef.EMAIL_VERIFICATION_SUBJECT, BODY);
                logger.info("email successfully sent");

            } catch (Exception e) {
                logger.error("email err:"+ e.getMessage());
            }
        }

        try{
            UserActivity activityUser = new UserActivity(userRes, username, ActivityAction.INSERT, userRes.getCreateTime());
            activityUserRepository.save(activityUser);
            return resUserBean;
        }catch (Exception e){
            logger.debug("Error :" + e.getMessage());
        }
        return null;
    }


    @Override
    public UserBean updateUser(UserBeanForUpdate userBeanForUpdate, Long id, String username) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ActrServiceException(HttpStatus.NOT_FOUND, "userid", "user.not.found");
        }
        logger.debug("User Found with id : {}", id);
        UserEntity existingUser = optionalUser.get();

        existingUser.setFirstName(userBeanForUpdate.getFirstName());
        existingUser.setLastName(userBeanForUpdate.getLastName());

        //existingUser.setUsername(userBean.getUsername());
        //existingUser.setEncryptedPassword(encoder.encode(userBean.getEncryptedPassword()));
        //existingUser.setEmail(userBean.getEmail());

        //existingUser.setUserCategory(userBean.getUserCategory());
        //existingUser.setStatus(userBean.getStatus());
        //existingUser.setUserType(userBean.getUserType());
        Date now = new Date();
        existingUser.setEditedBy(username);
        existingUser.setEditTime(now);
        existingUser.setLastUpdatedTime(now);
        logger.debug("updating user");
        UserEntity updateRes = userRepository.saveAndFlush(existingUser);
        logger.debug("user update done");

        logger.debug("user update done, adding activity...");
        UserActivity userActivity = new UserActivity(updateRes, username, ActivityAction.UPDATE, updateRes.getEditTime());
        activityUserRepository.save(userActivity);
        logger.debug("activitySaved, returning response");

        return getUserBean(updateRes);
    }

    @Override
    public UserBean activateUser( Long id, String otp) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new ActrServiceException(HttpStatus.NOT_FOUND, "userid", "user.not.found");
        }
        logger.debug("User Found with id : {}", id);
        UserEntity existingUser = optionalUser.get();
        if(existingUser.getStatus().equals(Status.ACTIVE)){
            throw new ActrServiceException(HttpStatus.ALREADY_REPORTED, "id", "user.email.verification.already.done");
        }
        Date now=new Date();
        long diff = now.getTime() - existingUser.getLastUpdatedTime().getTime();
        long diffMinutes = diff / (60 * 1000) % 60;
        existingUser.setLastUpdatedTime(now);
        logger.debug("validating otp : {}", otp);
        //logger.debug("validating otp with existing otp: {}", existingUser.getOtp());
        logger.debug("validating otp with diff minutes: {}", diffMinutes);

        if(existingUser.getOtp().equals(otp) && diffMinutes <= RegistrationDef.verificationTimeLimitInMinutes){

            existingUser.setStatus(Status.ACTIVE);
            logger.debug("activating user");
            UserEntity updateRes = userRepository.saveAndFlush(existingUser);
            logger.debug("user activate done");

            logger.debug("user activate done, adding activity...");
            UserActivity userActivity = new UserActivity(updateRes, existingUser.getUsername(), ActivityAction.ACTIVATE, updateRes.getEditTime());
            activityUserRepository.save(userActivity);
            logger.debug("activitySaved, returning response");

            return getUserBean(updateRes);
        }else{
            logger.debug("validation otp failed : {}", otp);
            return null;
        }
    }

    public Pair<List<UserBean>, Long> getAll(PaginationParam param, /*UserCategory userCategory,*/ Long organizationId) {
        Page<UserEntity> pageableUsers = getPageableUsers(param, /*userCategory,*/ organizationId);
        return Pair.of(pageableUserToUserBean(pageableUsers), pageableUsers.getTotalElements());
    }

    private List<UserBean> pageableUserToUserBean(Page<UserEntity> users) {
        return users.stream()
                .map(this::getUserBean)
                .collect(Collectors.toList());
    }

    private Page<UserEntity> getPageableUsers(PaginationParam param, /*UserCategory userCategory,*/ Long organizationId) {
        return organizationId != null ? getPageableSubUserByOrganizationId(param, /*userCategory,*/ organizationId) : getPageableAllUser(param/*, userCategory*/);
    }

    private Page<UserEntity> getPageableSubUserByOrganizationId(PaginationParam param, /*UserCategory userCategory,*/ Long organizationId) {
        PageRequest pageable = Util.parsePaginationParam(param);
        return userRepository.findAllUserByOrganizationIdAndUsernameIgnoreCaseOrderByUsernameAsc(/*userCategory.getCategory(),*/ organizationId, param.getSearchParamTrimmed(), pageable);
        //return null;
    }

    private Page<UserEntity> getPageableAllUser(PaginationParam param/*, UserCategory userCategory*/) {
        PageRequest pageable = Util.parsePaginationParam(param);
        return param.isSearchParamPresent()
                ? userRepository.findAllByUsernameContainingIgnoreCaseOrderByUsernameAsc(/*userCategory,*/
                param.getSearchParamTrimmed(), pageable)
                : userRepository.findAllByOrderByUsernameAsc(/*userCategory,*/ pageable);

    }

    @Override
    public boolean deactivateUser(String username, Long subUserId) {
        /*Optional<UserEntity> user = userRepository.findById(subUserId);
        if (user.isPresent()) {
            user.get().setStatus(Status.INACTIVE);
            UserEntity updateRes = userRepository.saveAndFlush(user.get());
            ActivityUser activityUser = new ActivityUser(updateRes, username, ActivityAction.UPDATE, updateRes.getEditTime());
            activityUserRepository.save(activityUser);
            return true;
        }

        return false;*/
        return false;
    }

    @Override
    public List<String> getListOfRoles(String username) {
        /*List<MethodAccess> accessList = userRepository.findMethodAccessListByUsername(username);
        return Util.methodAccessListToUniqueRoles(accessList);*/
        return null;
    }

    @Override
    public boolean updatePassword(String username, PasswordUpdateRequest updateReq) {
        /*logger.debug("updatePassword service called with username: {} and PaswordUpdateRequest: {}", username, updateReq.toString());
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            logger.debug("User found with the username: {}, fullName: {}", username, user.getFullName());
            if (!encoder.matches(updateReq.getCurrentPassword(), user.getPassword())) {
                throw new PasswordMismatchException("user.password", "error.wrong.password");
            } else if (!updateReq.getNewPassword().equals(updateReq.getConfirmPassword())) {
                throw new PasswordMismatchException("field.confirm.password", "error.new.password");
            } else if (updateReq.getNewPassword().equals(updateReq.getCurrentPassword())) {
                throw new PasswordMismatchException("user.password", "error.same.old.new.password");
            } else {
                user.setPassword(encoder.encode(updateReq.getNewPassword()));
                UserEntity updateRes = userRepository.saveAndFlush(user);
                ActivityUser activityUser = new ActivityUser(updateRes, username, ActivityAction.UPDATE, updateRes.getEditTime());
                activityUserRepository.save(activityUser);
                return true;
            }
        } else {
            throw new UserNotFoundException("error.user.not.found");
        }*/
        return false;
    }

    public UserBean findByUserName(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        return optionalUser.map(this::getUserBean).orElse(null);
    }

    @Override
    public UserBean findByEmail(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(this::getUserBean).orElse(null);
    }

    @Override
    public boolean resetPassword(PasswordResetRequest resetRequest, String adminUserName, Long userId) {
        logger.debug("ResetPassword service called with username: {} and PasswordResetRequest: {}",
                adminUserName, resetRequest.toString());
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            logger.debug("User Found With The UserName: {}", user.getUsername());

            if (!resetRequest.getNewPassword().equals(resetRequest.getConfirmPassword())) {
                throw new PasswordMismatchException("field.confirm.password", "error.new.password");
            } else {
                user.setEncryptedPassword(encoder.encode(resetRequest.getNewPassword()));
                UserEntity updateRes = userRepository.saveAndFlush(user);
                //ActivityUser activityUser = new ActivityUser(updateRes, adminUserName, ActivityAction.UPDATE, updateRes.getEditTime());
                //activityUserRepository.save(activityUser);
                return true;
            }
        } else {
            throw new UserNotFoundException("error.userBean.not.found");
        }
    }

    @Override
    public boolean changePassword(PasswordResetRequest resetRequest, String adminUserName, Long userId) {
        logger.debug("ResetPassword service called with username: {} and PasswordResetRequest: {}",
                adminUserName, resetRequest.toString());
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            logger.debug("User Found With The UserName: {}", user.getUsername());

            if (!resetRequest.getNewPassword().equals(resetRequest.getConfirmPassword())) {
                throw new PasswordMismatchException("field.confirm.password", "error.new.password");
            } else {
                user.setEncryptedPassword(encoder.encode(resetRequest.getNewPassword()));
                UserEntity updateRes = userRepository.saveAndFlush(user);
                //ActivityUser activityUser = new ActivityUser(updateRes, adminUserName, ActivityAction.UPDATE, updateRes.getEditTime());
                //activityUserRepository.save(activityUser);
                return true;
            }
        } else {
            throw new UserNotFoundException("error.userBean.not.found");
        }
    }

    @Override
    public boolean forgetPassword(PasswordResetRequest resetRequest, String adminUserName, Long userId) {
        logger.debug("ResetPassword service called with username: {} and PasswordResetRequest: {}",
                adminUserName, resetRequest.toString());
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            logger.debug("User Found With The UserName: {}", user.getUsername());

            if (!resetRequest.getNewPassword().equals(resetRequest.getConfirmPassword())) {
                throw new PasswordMismatchException("field.confirm.password", "error.new.password");
            } else {
                user.setEncryptedPassword(encoder.encode(resetRequest.getNewPassword()));
                UserEntity updateRes = userRepository.saveAndFlush(user);
                //ActivityUser activityUser = new ActivityUser(updateRes, adminUserName, ActivityAction.UPDATE, updateRes.getEditTime());
                //activityUserRepository.save(activityUser);
                return true;
            }
        } else {
            throw new UserNotFoundException("error.userBean.not.found");
        }
    }

    @Override
    public boolean expirePassword(Long userId, String adminUserName) {
        /*UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("error.userBean.not.found"));
        String newPassword = RandomStringUtils.randomAlphanumeric(8);
        user.setPassword(encoder.encode(newPassword));
        UserEntity updateRes = userRepository.saveAndFlush(user);
        ActivityUser activityUser = new ActivityUser(updateRes, adminUserName, ActivityAction.UPDATE, updateRes.getEditTime());
        activityUserRepository.save(activityUser);
        String smsTemplate = messageSource.getMessage("password.expire.sms",
                new Object[]{user.getUsername(), newPassword}, LocaleContextHolder.getLocale());
        logger.debug("Sending new password to username {}, mobile {}", user.getUsername(), user.getMobile());
        smsSender.send(
                new SMS(user.getMobile(), smsTemplate,  SMSUtil.getRandomString())
        );
        return true;*/
        return false;
    }

    private void checkUserExistenceByUsernameAndEmail(UserBean userBean) {
        UserBean byUserName = this.findByUserName(userBean.getUsername().trim().toLowerCase());
        UserBean byEmail = this.findByEmail(userBean.getEmail());
        if (byUserName != null) {
            throw new ActrServiceException(HttpStatus.BAD_REQUEST, "username", "error.username.already.exists");
        } else if (byEmail != null) {
            throw new ActrServiceException(HttpStatus.BAD_REQUEST, "email", "error.email.already.exists");
        }

    }

}
