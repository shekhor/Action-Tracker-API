//package com.tigerit.soa.loginsecurity.validator;
//
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.validation.Errors;
//import org.springframework.validation.ValidationUtils;
//import org.springframework.validation.Validator;
//import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//public class PermissionValidator implements Validator {
//
//    private final SpringValidatorAdapter validator;
//
//    private final PermissionRepository permissionRepository;
//
//    public PermissionValidator(SpringValidatorAdapter validator, PermissionRepository permissionRepository) {
//        super();
//        this.validator = validator;
//        this.slaRepository = slaRepository;
//    }
//
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return clazz.isAssignableFrom(SlaBean.class);
//    }
//
//    @Override
//    public void validate(Object target, Errors errors) {
//        SlaBean slaBean = (SlaBean) target;
//        validator.validate(slaBean, errors);
//        if (!errors.hasFieldErrors("name")) {
//            List<ServiceLayerAgreement> serviceLayerAgreementList;
//            if (Optional.ofNullable(slaBean.getId()).isPresent())
//                serviceLayerAgreementList = slaRepository.findByNameAndIdNot(slaBean.getName(), slaBean.getId());
//            else
//                serviceLayerAgreementList = slaRepository.findByName(slaBean.getName());
//            if (!CollectionUtils.isEmpty(serviceLayerAgreementList))
//                errors.rejectValue("name", "error.duplicate.sla",
//                        new String[]{slaBean.getName()}, null);
//        }
//        if (!MapUtils.isEmpty(slaBean.getResponseSettings())
//                && slaBean.getResponseSettings().keySet().contains(ApiEndPointEnum.VOTER_DETAILS_API)
//                && CollectionUtils.isEmpty((Collection<?>) slaBean.getResponseSettings().get(ApiEndPointEnum.VOTER_DETAILS_API))) {
//
//            errors.rejectValue("responseSettings",
//                    "error.response.settings.empty",
//                    null);
//
//            ValidationUtils.rejectIfEmpty(errors, "requestSettings",
//                    "error.request.settings.empty",
//                    "Select at least one request field");
//        }
//
//        if (!MapUtils.isEmpty(slaBean.getRequestSettings())
//                && slaBean.getRequestSettings().keySet().contains(ApiEndPointEnum.VOTER_DETAILS_API)
//                && CollectionUtils.isEmpty((Collection<?>) slaBean.getRequestSettings().get(ApiEndPointEnum.VOTER_DETAILS_API))) {
//
//            errors.rejectValue("requestSettings",
//                    "error.request.settings.empty",
//                    null);
//
//            ValidationUtils.rejectIfEmpty(errors, "responseSettings",
//                    "error.response.settings.empty",
//                    "select at least one response field");
//        }
//    }
//}