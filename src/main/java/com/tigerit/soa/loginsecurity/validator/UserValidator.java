package com.tigerit.soa.loginsecurity.validator;

import com.tigerit.soa.loginsecurity.models.request.UserBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Locale;

import static com.tigerit.soa.loginsecurity.util.ValidationUtils.*;

@Component
public class UserValidator implements Validator {
    private final SpringValidatorAdapter validator;

    private final MessageSource messageSource;

    public UserValidator(SpringValidatorAdapter validator,
                         MessageSource messageSource) {
        super();
        this.validator = validator;
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(UserBean.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserBean userBean = (UserBean) target;
        validator.validate(userBean, errors);

        /*if (!errors.hasFieldErrors("designation") && StringUtils.isNotEmpty(userBean.getDesignation())) {
            if (!userBean.getDesignation().matches(ALPHANUMERIC_SPACE_HYPHEN_UNDERSCORE_DOT)) {
                errors.rejectValue("designation",
                        "error.designation.invalid",
                        null);
            }
            if (userBean.getDesignation().length() > MAX_DESIGNATION_SIZE) {
                errors.rejectValue("designation",
                        "error.designation.max.size",
                        null);
            }
        }*/

        /*if (!errors.hasFieldErrors("nid") && StringUtils.isNotEmpty(userBean.getNid())) {
            if (!errors.hasFieldErrors("nid") && (userBean.getNid().length() != 10 && userBean.getNid().length() != 17)) {
                errors.rejectValue("nid",
                        "error.nid.invalid", new Integer[]{10, 17},
                        null);
            }
            if (!errors.hasFieldErrors("nid") && !StringUtils.isNumeric(userBean.getNid())) {
                errors.rejectValue("nid",
                        "error.nid.invalid", new Integer[]{10, 17},
                        null);
            }
        }*/

        /*if (!errors.hasFieldErrors("phone") && StringUtils.isNotEmpty(userBean.getPhone())) {
            if (!StringUtils.isNumeric(userBean.getPhone())) {
                errors.rejectValue("phone",
                        "error.phone.invalid",
                        null);
            }
            if (userBean.getPhone().length() > MAX_PHONE_SIZE) {
                errors.rejectValue("phone",
                        "error.phone.max.size",
                        null);
            }
        }*/

        /*if (!errors.hasFieldErrors("mobile") && StringUtils.isNotEmpty(userBean.getMobile())) {
            if (!StringUtils.isNumeric(userBean.getMobile())) {
                errors.rejectValue("mobile",
                        "error.mobile.invalid",
                        null);
            }
            if (userBean.getMobile().length() > MAX_MOBILE_SIZE) {
                errors.rejectValue("mobile",
                        "error.mobile.max.size",
                        null);
            }
        }*/
    }

    public String validatePassword(UserBean userBean, Locale locale) {
        String password = userBean.getEncryptedPassword();

        if (StringUtils.isEmpty(password)) {
            return messageSource.getMessage("error.password.null", null, locale);
        }
        if (password.length() > MAX_PASSWORD_SIZE) {
            return messageSource.getMessage("error.password.max.size", null, locale);
        }
        if (password.length() < MIN_PASSWORD_SIZE) {
            return messageSource.getMessage("error.password.min.size", null, locale);
        }
        if(!userBean.getEncryptedPassword().equals(userBean.getConfirmEncryptedPassword())){
            return messageSource.getMessage("error.password.mismatch", null, locale);
        }
        return null;
    }
}
