package com.tigerit.soa.serviceImpl;

import com.tigerit.soa.entity.es.OrganizationEntity;
import com.tigerit.soa.entity.es.OrganizationHistoryEntity;
import com.tigerit.soa.repository.es.DepartmentRepository;
import com.tigerit.soa.repository.es.OrganizationHistoryRepository;
import com.tigerit.soa.repository.es.OrganizationRepository;
import com.tigerit.soa.request.GetOrganizationRequest;
import com.tigerit.soa.request.OrganizationCreateRequest;
import com.tigerit.soa.request.OrganizationUpdateRequest;
import com.tigerit.soa.response.*;
import com.tigerit.soa.service.OrganizationService;
import com.tigerit.soa.util.RedisKey;
import com.tigerit.soa.util.RedisUtil;
import com.tigerit.soa.util.Status;
import com.tigerit.soa.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by DIPU on 4/8/20
 * Updated by Fahim on 4/9/20
 */
@Service
@Log4j2
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    OrganizationHistoryRepository organizationHistoryRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    RedisUtil redisUtil;

    @Override
    @CacheEvict(value = "organization", allEntries=true)
    public ServiceResponse createOrganization(OrganizationCreateRequest request, String userName) {
        try {
            OrganizationEntity organizationEntity = organizationRepository.findByDomainNameAndStatus(
                    request.getDomainName(), Status.ACTIVE.name());

            if(Objects.nonNull(organizationEntity)) {
                OrganizationResponse response = new OrganizationResponse();
                Util.copyProperty(organizationEntity, response);

                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
            }

            organizationEntity = new OrganizationEntity();

            Util.copyProperty(request, organizationEntity);
            String id = String.valueOf(redisUtil.getNextId(RedisKey.ORGANIZATION_KEY,RedisKey.ORGANIZATION_INITIAL_VALUE));
            organizationEntity.setId(id);
            organizationEntity.setTimeAndUser(userName);
            organizationEntity.setStatus(Status.ACTIVE.name());
            organizationEntity.setType("PRIVATE");
            organizationEntity.setCreatedBy(userName);

            organizationEntity = organizationRepository.indexWithoutRefresh(organizationEntity);
            log.info("Organization created");
            updateOrganizationHistory(organizationEntity);

            OrganizationResponse response = new OrganizationResponse();
            Util.copyProperty(organizationEntity, response);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
        }
    }



    @Override
    @Transactional
    @CacheEvict(value = {"organization"}, allEntries=true)
    public ServiceResponse updateOrganization(OrganizationUpdateRequest request, String userName) {
        try {
            log.info("Updating organization " + request.getId());
            OrganizationEntity entity = organizationRepository.findByIdAndDomainName(request.getId(),
                    request.getDomainName());

            if (Objects.isNull(entity)) {
                log.error("Organization not found");
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("organization.not.found", null, Locale.getDefault()),
                        "orgCode", "Validation failed");
                List<ErrorModel> errorList = new ArrayList<>();
                errorList.add(errorModel);
                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
            }

            Util.copyProperty(request, entity);
            entity.setTimeAndUser(userName);
            entity.setVersionId(entity.getVersionId()+1);

            entity = organizationRepository.save(entity);
            log.info("Organization updated");
            updateOrganizationHistory(entity);

            OrganizationResponse response = new OrganizationResponse();

            Util.copyProperty(entity, response);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
        } catch (DataIntegrityViolationException e) {
            log.error("Database error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "organization", allEntries=true)
    public ServiceResponse deleteOrganization(OrganizationUpdateRequest request, String userName) {
        try {
            log.info("Deleting organization " + request.getId());
            OrganizationEntity entity = organizationRepository.findByIdAndStatus(request.getId(), Status.ACTIVE.name());

            List<ErrorModel> errorList = validateDeleteOrganization(entity, request);

            if (errorList.size() > 0) {
                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
            }

            entity.setStatus(Status.DELETED.name());
            entity.setTimeAndUser(userName);
            entity.setVersionId(entity.getVersionId()+1);

            entity = organizationRepository.save(entity);
            log.info("Organization deleted");
            updateOrganizationHistory(entity);

            OrganizationResponse response = new OrganizationResponse();

            Util.copyProperty(entity, response);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
        } catch (DataIntegrityViolationException e) {
            log.error("Database error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
        }
    }

    private List<ErrorModel> validateDeleteOrganization(OrganizationEntity entity, OrganizationUpdateRequest request) {

        List<ErrorModel> errorList = new ArrayList<>();
        try {
            if (Objects.isNull(entity)) {
                log.error("Organization not found");
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("organization.not.found", null, Locale.getDefault()),
                        "orgCode", "Validation failed");
                errorList.add(errorModel);
            }

            if (Objects.nonNull(entity) && !entity.getOrganizationName().equalsIgnoreCase(request.getOrganizationName())) {
                log.error("Organization name did not match");
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("organization.name.not.match", null, Locale.getDefault()),
                        "orgCode", "Validation failed");
                errorList.add(errorModel);
            }

//            Integer count = departmentRepository.countByOrganizationId(request.getId());
//
//            if (count > 0) {
//                log.error("Organization have active departments");
//                ErrorModel errorModel = new ErrorModel(
//                        messageSource.getMessage("organization.have.active.department", null, Locale.getDefault()),
//                        "orgCode", "Validation failed");
//                errorList.add(errorModel);
//            }
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            errorList.add(errorModel);
        }
        return errorList;
    }

    @Override
    @Cacheable(value = "organization", key = "#request", unless = "#request.totalItemPerPage>50")
    public ServiceResponse getAllOrganization(GetOrganizationRequest request, String userName) {
        try {
            log.info("Getting list of organization");

            //Sort sort = Sort.by("id").ascending();
            Pageable pageRequest = PageRequest.of(request.getPageNo(), request.getTotalItemPerPage());

            Page<OrganizationEntity> organizationEntityPage = organizationRepository.findAllByTypeAndStatus(
                    "PRIVATE",Status.ACTIVE.name(), pageRequest);

            GetOrganizationResponse response = new GetOrganizationResponse();
            response.setOrganizationEntityList(organizationEntityPage.getContent());
            response.setTotalItem(organizationEntityPage.getTotalElements());

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, response, Collections.emptyList());
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);
            clearCache();

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
        }
    }

    @Override
    @Cacheable(value = "organization", key = "#organizationId")
    public ServiceResponse getOrganizationById(String organizationId, String userName) {
        try {
            log.info("Getting organization for " + organizationId);
            Optional<OrganizationEntity> organizationEntity = organizationRepository.findById(organizationId);

            if (organizationEntity.isPresent()) {
                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, organizationEntity.get(), Collections.emptyList());
            } else {
                log.error("Organization not found");
                ErrorModel errorModel = new ErrorModel(
                        messageSource.getMessage("organization.not.found", null, Locale.getDefault()),
                        "orgCode", "ID not matched");
                List<ErrorModel> errorList = new ArrayList<>();
                errorList.add(errorModel);
                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
            }
        } catch (Exception e) {
            log.error("Internal server error " + e.getMessage());
            ErrorModel errorModel = new ErrorModel(
                    messageSource.getMessage("internal.server.error", null, Locale.getDefault()),
                    "create", e.getMessage());
            List<ErrorModel> errorList = new ArrayList<>();
            errorList.add(errorModel);
            clearCache();

            return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, null, errorList);
        }
    }

    @CacheEvict(value = "organization", allEntries=true)
    public void clearCache() {
        System.out.println("cache cleared");
    }

    private void updateOrganizationHistory(OrganizationEntity organizationEntity) {
        OrganizationHistoryEntity organizationHistoryEntity = new OrganizationHistoryEntity();

        String id = String.valueOf(redisUtil.getNextId(RedisKey.ORGANIZATION_HISTORY_KEY,
                RedisKey.ORGANIZATION_HISTORY_INITIAL_VALUE));

        Util.copyProperty(organizationEntity, organizationHistoryEntity);
        organizationHistoryEntity.setId(id);
        organizationHistoryEntity.setOrganizationIndexId(organizationEntity.getId());

        organizationHistoryRepository.save(organizationHistoryEntity);

        log.debug(organizationEntity.getDomainName() +" history added");
    }
}
