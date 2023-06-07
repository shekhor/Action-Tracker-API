package com.tigerit.soa.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.tigerit.soa.entity.es.DepartmentEntity;
import com.tigerit.soa.entity.es.TestUser;
import com.tigerit.soa.loginsecurity.models.request.UserBean;
import com.tigerit.soa.loginsecurity.service.UserService;
import com.tigerit.soa.model.PaginationInfo;
import com.tigerit.soa.model.es.Department;
import com.tigerit.soa.model.es.SearchResult;
import com.tigerit.soa.repository.es.DepartmentRepository;
import com.tigerit.soa.repository.es.OrganizationRepository;
import com.tigerit.soa.request.es.DepartmentRequest;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.DepartmentService;
import com.tigerit.soa.util.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by DIPU on 4/8/20
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserService userService;

    @Autowired
    private OrganizationRepository orgRepository;

    @Qualifier("elasticsearchTemplate")
    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Override
    public ServiceResponse test() {

        Iterable<DepartmentEntity> source= departmentRepository.findAll();
        List<DepartmentEntity> list=new ArrayList<>();
        source.forEach(list::add);

        return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, list, Collections.emptyList());
    }

    @Override
    public ServiceResponse createDepartment(DepartmentRequest departmentRequest, UserDetails userDetails, Locale locale)
    {

       try
       {
           logger.info("createDepartment: operation start...");

           Optional<DepartmentEntity> deptOptional= departmentRepository.findByDepartmentNameAndOrganizationId(departmentRequest.getDepartmentName(), departmentRequest.getOrganizationId());
           if(deptOptional.isPresent())
           {
               DepartmentEntity dbOb= deptOptional.get();
                logger.info("duplicate dept found with same name={} and orgId={}", dbOb.getDepartmentName(), dbOb.getOrganizationId());
                String errorMessage=messageSource.getMessage("department.error.duplicate.found", null, locale);
                return new ServiceResponse(HttpStatus.CONFLICT, StatusCode.ERROR, null, new ArrayList<String>(Arrays.asList(errorMessage)));
           }

           logger.info("save dept: start...");

           DepartmentEntity dbOb= new DepartmentEntity();
           Util.copyProperty(departmentRequest, dbOb);
           String deptId = String.valueOf(redisUtil.getNextId(RedisKey.DEPARTMENT_KEY,
                   RedisKey.DEPARTMENT_INITIAL_KEY_VALUE));
           dbOb.setId(deptId);
           dbOb.setCreatedBy(userDetails.getUsername());
           dbOb.setCreateTime(new Date());
           dbOb.setCreateTimeInMs((new Date()).getTime());
           dbOb.setStatus(Status.ACTIVE.name());
           dbOb.setVersionId(Defs.VERSION_ID);

           dbOb=departmentRepository.save(dbOb);

           logger.info("save dept: end");

           Department deptDto= new Department();
           Util.copyProperty(dbOb, deptDto);

           return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, deptDto, Collections.emptyList());
       }
       catch (Exception e)
       {
            logger.error("unexpected err:"+e.getMessage());
       }

        String errorMessage=messageSource.getMessage("department.create.error.unexpected", null, locale);
        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null, new ArrayList<String>(Arrays.asList(errorMessage)));
    }

    @Override
    public ServiceResponse editDepartment(DepartmentRequest deptPayload, UserDetails userDetails, Locale locale) {

        logger.info("updateDepartment: operation start...");
        try
        {
            DepartmentEntity deptDO=departmentRepository.findByIdAndOrganizationId(deptPayload.getId(),deptPayload.getOrganizationId());
            if(!Objects.isNull(deptDO))
            {
                logger.info("department found to update with id:"+ deptPayload.getId());

                //TODO:cross check with inter org dept--->> ***done: do we really need to check this??
               /* UserBean userEntity=userService.findByUserName(userDetails.getUsername());
                OrganizationEntity orgEntity;
                boolean isDeptOwner, isOrgOwner=false;
                if(userEntity.getId().longValue()==deptPayload.getDepartmentOwner().longValue())
                    isDeptOwner=true;
                else isDeptOwner=false;
                if(!isDeptOwner)
                {
                    Optional<OrganizationEntity> orgEntityOptional=orgRepository.findById(deptPayload.getOrganizationId());
                    if(orgEntityOptional.isPresent())
                    {
                        orgEntity= orgEntityOptional.get();
                        if(Util.isEqualIgnoredCaseStr(userDetails.getUsername(), orgEntity.getOrganizationOwner()))
                            isOrgOwner=true;
                        else isOrgOwner=false;
                    }
                    else isOrgOwner=false;
                }

               if(!isDeptOwner && !isOrgOwner)
               {
                   logger.debug("Unauthorized user operation-dept update for dept.Id: {}", deptPayload.getId());
                   return new ServiceResponse(HttpStatus.FORBIDDEN, StatusCode.ERROR, null,
                           new ArrayList<String>(Arrays.asList("User must be Department-Owner or Organization-Owner to update a department!")));
               }
*/
                deptDO.setDepartmentName(deptPayload.getDepartmentName());
                deptDO.setDepartmentOwner(deptPayload.getDepartmentOwner());
                deptDO.setDescription(deptPayload.getDescription());
               // deptDO.setOrganizationId(deptPayload.getOrganizationId());
                deptDO.setStatus(deptPayload.getStatus());
                deptDO.setVersionId(deptDO.getVersionId()+1L);
                deptDO.setEditedBy(userDetails.getUsername());
                deptDO.setEditTime(new Date());
                deptDO.setEditTimeInMs((new Date()).getTime());

                logger.info("update department...");
                deptDO=departmentRepository.save(deptDO);

                Department deptDto= new Department();
                Util.copyProperty(deptDO, deptDto);

                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, deptDto, Collections.emptyList());
            }
            else
            {
                logger.info("no dept found with id: {} under org.Id: {}", deptPayload.getId(),deptPayload.getOrganizationId());
                return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null,
                        new ArrayList<String>(Arrays.asList("Department not found!")));
            }
        }
        catch (Exception e)
        {
            logger.error("updateDept err:"+e.getMessage());
        }

        String errorMessage=messageSource.getMessage("department.update.error.unexpected", null, locale);
        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null, new ArrayList<String>(Arrays.asList(errorMessage)));
    }

    @Override
    public ServiceResponse getDepartmentDetailsById(String departmentId, String username) {
        logger.info("do cross validation for department and org");
        UserBean user=userService.findByUserName(username);
        if(!Objects.isNull(user))
        {
            if(user.getOrganizationId()!=null)
               logger.info("department need to lookup for deptId:{} and orgId: {}", departmentId, user.getOrganizationId().toString());
            DepartmentEntity dept= departmentRepository.findByIdAndOrganizationId(departmentId, user.getOrganizationId().toString());
            if(!Objects.isNull(dept))
            {
                Department deptDto= new Department();
                Util.copyProperty(dept, deptDto);
                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, deptDto, Collections.emptyList());
            }
            logger.info("no such department found of id : {} under your organization.", departmentId);
            return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null,
                    new ArrayList<String>(Arrays.asList("Department not found!")));
        }
        logger.info("something unusual happened to fetch department with id:{} , try refreshing again", departmentId);
        return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("Department not found!")));
    }

    @Override
    public ServiceResponseExtended getAllDepartmentPerOrg(PaginationInfo pageReq, String userName) {

        if(Util.isEmpty(pageReq.getSortBy()))
        {
            //pageReq.setSortBy("createTime");
            pageReq.setSortBy("id");
        }
        int pageNo=pageReq.getPageNo();
        int pageSize=pageReq.getPageSize();
        String sortBy=pageReq.getSortBy();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        try
        {
            //build query: all users see all dept--no restriction
            UserBean userBean=userService.findByUserName(userName);
            //ES query builder or spring data jpa? anyone
            if(!Objects.isNull(userBean)&& !Util.isEmpty(userBean.getOrganizationId().toString()))
            {
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                query.must(QueryBuilders.termQuery("organizationName", userBean.getOrganizationId().toString()));
                ServiceResponseExtended response = new ServiceResponseExtended();
                SearchQuery searchQuery = new NativeSearchQueryBuilder()
                        .withIndices("department")
                        .withQuery(query)
                        .build().setPageable(pageable);

                com.tigerit.soa.model.es.SearchResult result = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<SearchResult>() {

                    @Override
                    public com.tigerit.soa.model.es.SearchResult extract(SearchResponse response) {
                        long totalHits = response.getHits().getTotalHits();
                        logger.info("totalHits:" + totalHits);
                        List<Department> deptList = new ArrayList<Department>();
                        response.getHits().forEach(hit -> deptList.add(JSON.parseObject(hit.getSourceAsString(), (Type) Department.class)));

                        return new com.tigerit.soa.model.es.SearchResult(totalHits, deptList);
                    }
                });

                return new ServiceResponseExtended(HttpStatus.OK, StatusCode.SUCCESS, result.getResultList(), pageNo,
                        pageSize, result.getTotalHit());
            }

          /*  if(!Objects.isNull(userBean) && !Util.isEmpty(userBean.getOrganizationId().toString()))
            {
                logger.info("fetch department pages.");
                String orgId=userBean.getOrganizationId().toString();
                Page<DepartmentEntity> pageObject=departmentRepository.findByOrganizationId(orgId,  pageable);
                logger.info("pageInfo: {} totalpage: {}", pageObject.getTotalElements(), pageObject.getTotalPages());
                return new ServiceResponseExtended(HttpStatus.OK, StatusCode.SUCCESS, pageObject.getContent(), pageObject.getNumber(),
                        pageSize, pageObject.getTotalElements());
            }*/
            else
            {
                logger.info("invalid user!.");
                List<String>errorList= new ArrayList<String>(Arrays.asList("Unauthorized operation, please refresh the page and try again!"));
                ServiceResponseExtended response=new ServiceResponseExtended();
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setStatusCode(StatusCode.ERROR);
                response.setErrorList(errorList);

                return response;
            }

        }
        catch (Exception e)
        {
            logger.error("something unwanted happen:"+ e.getMessage());
        }


        List<String>errorList= new ArrayList<String>(Arrays.asList("Internal server error!"));
        ServiceResponseExtended response=new ServiceResponseExtended();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setStatusCode(StatusCode.ERROR);
        response.setErrorList(errorList);

        return response;
    }

    @Override
    public ServiceResponse archiveDepartment(String deptId, String userName) {

        UserBean userBean=userService.findByUserName(userName);
        try
        {
            if(!Objects.isNull(userBean))
            {
                DepartmentEntity deptDO=departmentRepository.findByIdAndOrganizationId(deptId, userBean.getOrganizationId().toString());
                if(Objects.isNull(deptDO))
                {
                    return new ServiceResponse(HttpStatus.NOT_FOUND, StatusCode.ERROR, null, new ArrayList<String>(Arrays.asList("Department not found!")));
                }
                //archive here
                deptDO.setStatus(Status.ARCHIVED.name());
                deptDO.setEditedBy(userName);
                deptDO.setEditTimeInMs((new Date()).getTime());
                deptDO.setEditTime(new Date());

                logger.info("archiving dept of id:{}, name:{}", deptDO.getId(), deptDO.getDepartmentName());
                deptDO=departmentRepository.save(deptDO);
                logger.info("department:{} with id:{} is archived by:{} orgId:{}",deptDO.getDepartmentName(),
                        deptDO.getId(), userName, userBean.getOrganizationId());

                Department deptDto= new Department();
                Util.copyProperty(deptDO, deptDto);

                return new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS, deptDto, Collections.emptyList());
            }
            return new ServiceResponse(HttpStatus.FORBIDDEN, StatusCode.ERROR, null, new ArrayList<String>(Arrays.asList("Invalid user. Please refresh the page and try again!")));

        }
        catch (Exception e)
        {
            logger.error("error dept archive:"+ e.getMessage());
        }
        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("Internal server error!")));
    }
}
