//package com.tigerit.soa.controller;
//
////import com.tigerit.soa.nid2.partnerservice.component.logger.Loggable;
////import com.tigerit.soa.nid2.partnerservice.model.PaginationParam;
////import com.tigerit.soa.nid2.partnerservice.model.bean.SlaBean;
////import com.tigerit.soa.nid2.partnerservice.model.bean.SlaInfoBean;
////import com.tigerit.soa.nid2.partnerservice.model.response.RestApiResponse;
////import com.tigerit.soa.nid2.partnerservice.model.response.SlaApiResponse;
////import com.tigerit.soa.nid2.partnerservice.service.sla.SlaService;
////import com.tigerit.soa.nid2.partnerservice.utils.SessionKey;
////import com.tigerit.soa.nid2.partnerservice.utils.Utils;
////import com.tigerit.soa.nid2.partnerservice.validator.SlaValidator;
////import io.swagger.v3.oas.annotations.Operation;
////import io.swagger.v3.oas.annotations.responses.ApiResponse;
////import io.swagger.v3.oas.annotations.responses.ApiResponses;
////import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.slf4j.Logger;
//import org.springframework.context.MessageSource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//import java.util.List;
//import java.util.Locale;
//
//@RestController
//@RequestMapping(path = "/rest/permission")
///*
//@ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Success"),
//        @ApiResponse(responseCode = "400", description = "Bad Request"),
//        @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
//        @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
//        @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
//        @ApiResponse(responseCode = "500", description = "Internal Server Error")
//})
//@SecurityRequirement(name = "bearer-auth")
//*/
//public class PermissionController {
//
//    /*@Loggable
//    private static Logger logger;
//*/
//    /*private SlaService slaService;
//
//    private SlaValidator slaValidator;
//
//    private MessageSource messageSource;
//
//    public SlaController(SlaService slaService,
//                         MessageSource messageSource,
//                         SlaValidator slaValidator) {
//        this.slaService = slaService;
//        this.messageSource = messageSource;
//        this.slaValidator = slaValidator;
//    }*/
//
//    @InitBinder("slaBean")
//    protected void initBinder(WebDataBinder binder) {
//        binder.setValidator(slaValidator);
//    }
//
//    @RequestMapping(path = "/details/{id}", method = RequestMethod.GET)
//    @PreAuthorize("hasAnyRole('PRIVILEGE_DETAILS_BY_ID')")
//    //@Operation(summary = "Get SLA Details By ID", description = "SLA_DETAILS_BY_ID Privilege Is Required")
//    public ResponseEntity<RestApiResponse<SlaApiResponse>> getSLADetails(@PathVariable(name = "id") Long slaId,
//                                                                         HttpServletRequest request, Locale locale) {
//
//        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
//        logger.debug("Get SLA Details request from: {} with id: {}", userDetails.getUsername(), slaId);
//        SlaBean slaBean = slaService.findDetailById(slaId);
//        SlaApiResponse slaApiResponse = new SlaApiResponse(slaBean);
//        RestApiResponse<SlaApiResponse> apiResponse = Utils.buildSuccessRestResponse(HttpStatus.OK, slaApiResponse);
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @RequestMapping(path = "/list", method = RequestMethod.POST)
//    @PreAuthorize("hasAnyRole('SLA_LIST')")
//    //@Operation(summary = "Fetch Paginated SLA List", description = "SLA_LIST Privilege Is Required")
//    public ResponseEntity<RestApiResponse<SlaApiResponse>> getSLAList(@RequestBody PaginationParam param,
//                                                                      HttpServletRequest request) {
//        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
//        logger.debug("Fetch Paginated SLA List request from: {} with PaginationParam: {}",
//                userDetails.getUsername(), param.toString());
//        List<SlaBean> slaBeans = slaService.getSlaListWithPagination(param);
//        Long slaCount = slaService.getSlaCount(param);
//        SlaApiResponse slaApiResponse = new SlaApiResponse(slaBeans, slaCount);
//        RestApiResponse<SlaApiResponse> apiResponse = Utils.buildSuccessRestResponse(HttpStatus.OK, slaApiResponse);
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @RequestMapping(path = "/create", method = RequestMethod.POST)
//    @PreAuthorize("hasAnyRole('SLA_CREATE')")
//    //@Operation(summary = "Create New SLA", description = "SLA_CREATE Privilege Is Required")
//    public ResponseEntity<RestApiResponse<SlaApiResponse>> createSLA(@Valid @RequestBody SlaBean slaBean,
//                                                                     HttpServletRequest request) {
//        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
//        logger.debug("Create New SLA request from: {} with SlaBean: {}", userDetails.getUsername(), slaBean.toString());
//        slaBean = slaService.create(userDetails.getUsername(), slaBean);
//        SlaApiResponse slaApiResponse = new SlaApiResponse(slaBean);
//        RestApiResponse<SlaApiResponse> apiResponse = Utils.buildSuccessRestResponse(HttpStatus.OK, slaApiResponse);
//        return ResponseEntity.ok(apiResponse);
//    }
//
//    @RequestMapping(path = "/update/{id}", method = RequestMethod.POST)
//    @PreAuthorize("hasAnyRole('SLA_UPDATE')")
//    //@Operation(summary = "Update SLA By Id", description = "SLA_UPDATE Privilege Is Required")
//    public ResponseEntity<RestApiResponse<SlaApiResponse>> updateSLA(@PathVariable(name = "id") Long slaId,
//                                                                     @Valid @RequestBody SlaBean slaBean,
//                                                                     HttpServletRequest request, Locale locale) {
//        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
//        logger.debug("Update SLA request from: {} with id: {} and SlaBean: {}",
//                userDetails.getUsername(), slaId, slaBean.toString());
//        slaBean = slaService.update(userDetails.getUsername(), slaId, slaBean);
//        RestApiResponse<SlaApiResponse> apiResponse;
//        if (slaBean == null)
//            apiResponse = Utils.buildErrorRestResponse(HttpStatus.NOT_FOUND, "slaId",
//                    messageSource.getMessage("error.sla.not.found", new String[]{slaId.toString()}, locale));
//        else {
//            SlaApiResponse slaApiResponse = new SlaApiResponse(slaBean);
//            apiResponse = Utils.buildSuccessRestResponse(HttpStatus.OK, slaApiResponse);
//        }
//
//        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
//    }
//
//    @RequestMapping(path = "/inactive/{id}", method = RequestMethod.POST)
//    @PreAuthorize("hasAnyRole('SLA_INACTIVE')")
//    //@Operation(summary = "Inactive SLA By Id", description = "SLA_INACTIVE Privilege Is Required")
//    public ResponseEntity<RestApiResponse<Boolean>> inactiveSLA(@PathVariable(name = "id") Long slaId,
//                                                                HttpServletRequest request, Locale locale) {
//        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
//        logger.debug("Inactive SLA By Id request from: {} with id: {}", userDetails.getUsername(), slaId);
//        boolean success = slaService.inactiveSla(userDetails.getUsername(), slaId);
//        RestApiResponse<Boolean> apiResponse;
//        if (!success)
//            apiResponse = Utils.buildErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "id",
//                    messageSource.getMessage("error.sla.inactive.fail", new Long[]{slaId}, locale));
//        else {
//            apiResponse = Utils.buildSuccessRestResponse(HttpStatus.OK, success);
//        }
//        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
//    }
//
//    @RequestMapping(path = "/get-sla-list", method = RequestMethod.GET)
//    //@PreAuthorize("hasAnyRole('SLA_GET_LIST')")
//    @PreAuthorize("hasAnyRole('SLA_LIST')")
//    //@Operation(summary = "Get all sla list", description = "Need to have PARTNER_API Privilege")
//    public ResponseEntity<RestApiResponse<SlaApiResponse>> getAllSlaList(HttpServletRequest request) {
//        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(SessionKey.USER_DETAILS);
//        logger.debug("Get all sla list request from: {}", userDetails.getUsername());
//        List<SlaInfoBean> slaInfoBeans = slaService.getAllSlaListByStatus();
//        SlaApiResponse slaApiResponse = new SlaApiResponse(slaInfoBeans);
//        RestApiResponse<SlaApiResponse> apiResponse = Utils.buildSuccessRestResponse(HttpStatus.OK, slaApiResponse);
//        return ResponseEntity.ok(apiResponse);
//    }
//}
