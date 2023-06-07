package com.tigerit.soa.controller;

import com.alibaba.fastjson.JSON;
import com.tigerit.soa.entity.es.TestProject;
import com.tigerit.soa.entity.es.TestEsEntity;
import com.tigerit.soa.entity.es.TestEsEntity2;
import com.tigerit.soa.entity.es.TestUser;
import com.tigerit.soa.repository.es.TestEs2Repository;
import com.tigerit.soa.repository.es.TestEsRepository;
import com.tigerit.soa.repository.es.TestUserRepository;
import com.tigerit.soa.request.es.TestUserRequest;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;
import com.tigerit.soa.response.StatusCode;
import com.tigerit.soa.service.redis.RedisCacheService;
import com.tigerit.soa.util.RedisUtil;
import com.tigerit.soa.util.Util;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/*
Fahim created at 4/19/2020
*/
@RestController
@RequestMapping("/elasticsearch/test")
public class TestEsController {

    private Logger logger = LoggerFactory.getLogger(TestEsController.class);

    @Autowired
    private TestEsRepository esRepository;

    @Autowired
    private TestUserRepository testUserRepository;

    @Autowired
    private TestEs2Repository esRepository2;

    @Qualifier("elasticsearchTemplate")
    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    RedisCacheService redisCacheService;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(path = "/testEsUpdate/{taskId}/{value}", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> testEsUpdate(@PathVariable("taskId") String taskId,
                                                        @PathVariable("value") String value) {

        ServiceResponse response = new ServiceResponse();

        try {
            System.out.println("updating for " + value);
            synchronized (taskId.intern()) {
                System.out.println("inside sync for " + value);
                save(value);
                System.out.println("updated for " + value);

                response.setBody(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    @Transactional
    public synchronized void save(String value) {
        TestEsEntity esEntity = esRepository.findById("3").get();
        esEntity.setEmail("email");
        esEntity.setFirst_name("fahim" + value);
        TestProject testProject = new TestProject(6, value + value);
        List<TestProject> testProjects = esEntity.getTestProject();
        testProjects.add(testProject);

        esEntity.setTestProject(testProjects);

        esRepository.save(esEntity);
        System.out.println("Saved for " + value);
    }

    @RequestMapping(path = "/testEs", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> testEs() {

        ServiceResponse response = new ServiceResponse();

        try {
            String id = String.valueOf(redisUtil.getNextId("TEST", 10));
            logger.info("FOr id " + id);
            //save
            TestEsEntity esEntity = new TestEsEntity();
            esEntity.setId(id);
            esEntity.setEmail("email");
            esEntity.setFirst_name("fahim");
            TestProject testProject = new TestProject(1, "P1");
            TestProject testProject2 = new TestProject(1, "P2");
            List<TestProject> testProjects = new ArrayList<>();
            testProjects.add(testProject);
            testProjects.add(testProject2);
            esEntity.setTestProject(testProjects);

            esRepository.save(esEntity);

            //update
            esEntity.setEmail("fahim_email");
            esEntity.setFirst_name("fahim");
            TestProject testProject3 = new TestProject(1, "P2");
            testProjects.add(testProject2);
            esEntity.setTestProject(testProjects);

            esRepository.save(esEntity);


            //search by basic property
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(matchQuery("email", "fahim_email"))
                    .withFields("email", "first_name")
                    .withPageable(PageRequest.of(0,10))
                    .build();

            List<TestEsEntity> result = elasticsearchTemplate.queryForList(searchQuery, TestEsEntity.class);

            //serach by nested object property
//            QueryBuilder builder = nestedQuery("testProject",
//                    boolQuery().must(termQuery("testProject.id", "1")), ScoreMode.None);
//
//            NativeSearchQuery searchQuery2 = new NativeSearchQueryBuilder().withQuery(builder).build();

//            result = elasticsearchTemplate.queryForList(searchQuery, TestEsEntity.class);

            response.setBody(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/testEsForMap", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> testEsForMap() {

        ServiceResponse response = new ServiceResponse();

        try {
            //save
            TestEsEntity2 esEntity = new TestEsEntity2();
            esEntity.setId("3");
            esEntity.setEmail("email");
            esEntity.setFirstName("fahim");
            TestProject testProject = new TestProject(1, "P1");
            TestProject testProject2 = new TestProject(1, "P2");
            List<TestProject> testProjects = new ArrayList<>();
            testProjects.add(testProject);
            testProjects.add(testProject2);
            esEntity.setTestProject(testProjects);

            Map<String, String> risk = new HashMap<>();
            risk.put("k1", "v1");
            esEntity.setJson_field(risk);

            esRepository2.save(esEntity);

            //search by basic property
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(matchQuery("id", "1"))
                    .build();

            List<TestEsEntity2> result = elasticsearchTemplate.queryForList(searchQuery, TestEsEntity2.class);

            response.setBody(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/testEsSearch", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> testEsSearch() {

        ServiceResponse response = new ServiceResponse();

        try {
            BoolQueryBuilder builder = QueryBuilders.boolQuery();

            //AND Relation
            builder.must(new QueryStringQueryBuilder("fahim_email").field("email"));
            builder.must(new QueryStringQueryBuilder("fahim").field("first_name"));

            FieldSortBuilder sort = SortBuilders.fieldSort("id").order(SortOrder.DESC);

            //PageRequest page = new PageRequest(0, 10);

            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            nativeSearchQueryBuilder.withQuery(builder);
            nativeSearchQueryBuilder.withSort(sort);
            NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();
            //nativeSearchQueryBuilder.withPageable(page);

            List<TestEsEntity> result = elasticsearchTemplate.queryForList(searchQuery, TestEsEntity.class);

            //matchQuery -> match the text ignoring case / OR Relation
            NativeSearchQueryBuilder nsqb = new NativeSearchQueryBuilder();
            nsqb.withQuery(matchQuery("email", "fahim_email"));
            nsqb.withQuery(matchQuery("first_name", "fahim"));
            nsqb.withFields("email", "first_name");

            SearchQuery searchQuery2 = nsqb.build();
            List<TestEsEntity> result2 = elasticsearchTemplate.queryForList(searchQuery2, TestEsEntity.class);

            //termQuery -> Exact match
            QueryBuilder queryBuilder = QueryBuilders.termQuery("email", "fahim_email");
            SearchQuery searchQuery3 = nsqb.withQuery(queryBuilder).build();
            List<TestEsEntity> result3 = elasticsearchTemplate.queryForList(searchQuery3, TestEsEntity.class);

            //search multiple value in single field
            QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("email", "fahim_email", "salkdfj");
            SearchQuery searchQuery4 = nsqb.withQuery(queryBuilder2).build();
            List<TestEsEntity> result4 = elasticsearchTemplate.queryForList(searchQuery4, TestEsEntity.class);

            //search one value in multiple field
            QueryBuilder queryBuilder3 = QueryBuilders.multiMatchQuery("salkdfj", "first_name", "email");
            SearchQuery searchQuery5 = nsqb.withQuery(queryBuilder3).build();
            List<TestEsEntity> result5 = elasticsearchTemplate.queryForList(searchQuery5, TestEsEntity.class);

            //wildcard query
            QueryBuilder wildCardQuery = QueryBuilders.wildcardQuery("email", "*email*");
            SearchQuery searchQuery6 = nsqb.withQuery(wildCardQuery).build();
            List<TestEsEntity> result6 = elasticsearchTemplate.queryForList(searchQuery6, TestEsEntity.class);

            response.setBody(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    /*
     * Note: no validation check is done for brevity
     */
    //sample data:
    /*{
        "id":2,
            "name":"dipu2",
            "age":26,
            "projectList":
	[
        {
            "id":1,
                "name":"project1"
        },
        {
            "id":2,
                "name":"project2"
        },
        {
            "id":3,
                "name":"project3"
        }
	],
        "userSettings" :
        {
            "gender" : "male",
                "hobby":"amateur athlete, marathoner"
        }
    }*/
    @PostMapping("/user/create")
    public ResponseEntity<ServiceResponse> createUser(@RequestBody TestUser user) {
        ServiceResponse response;
        try {
            logger.info("save user doc with id:");
            TestUser savedUser = testUserRepository.save(user);
            response = new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS,
                    savedUser, Collections.EMPTY_LIST);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("error saving the doc:", e.getCause());
        }

        logger.info("couldn't save user doc with");
        response = new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, user,
                new ArrayList<String>(Arrays.asList("saving user doc error: could  not save returned object")));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/update/{id}")
    public ResponseEntity<ServiceResponse> updateUser(@PathVariable("id") Long userId, @RequestBody TestUser user) {
        ServiceResponse response;
        try {
            logger.info("fetch the old doc with id:" + userId);
            Optional<TestUser> testUserOptional = testUserRepository.findById(userId);
            if (testUserOptional.isPresent()) {

                TestUser entityObject = testUserOptional.get();
                entityObject.setAge(user.getAge());
                entityObject.setName(user.getName());
                //and others
                logger.info("prev object found with Id:" + entityObject.getId());

                TestUser updatedObj = testUserRepository.save(entityObject);
                response = new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS,
                        updatedObj, Collections.EMPTY_LIST);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("error saving the doc:", e.getCause());
        }

        logger.debug("couldn't update user doc with");
        response = new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, user,
                new ArrayList<String>(Arrays.asList("updating user doc error: could not save returned object")));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/all")
    public ResponseEntity<ServiceResponse> getAll() {
        ServiceResponse response;
        try {
            logger.info("fetch all doc");
            Iterable<TestUser> resultSet = testUserRepository.findAll();
            response = new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS,
                    resultSet, Collections.EMPTY_LIST);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("error fetching the doc:", e.getCause());
        }

        logger.debug("couldn't get user doc ");
        response = new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.ERROR, null,
                new ArrayList<String>(Arrays.asList("getting sadfsdfsdfs")));

        return ResponseEntity.ok(response);
    }


    //search request:
    /*{
        "name":"dipu",

            "userSettings" :
        {
            "gender" : "male",
                "hobby":"amateur athlete, dancer"
        }
    }*/
    @PostMapping("/user/search")
    public ResponseEntity<ServiceResponseExtended> multiParamSearch(@RequestBody TestUserRequest user,
                                                                    @RequestParam(defaultValue = "0") int pageNo,
                                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                                    @RequestParam(defaultValue = "id") String sortBy) {
        //TODO: more todo
        ServiceResponseExtended response;
        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        //with static query:
      /* List<SearchQuery> queryList=new ArrayList<>();
       if(!Util.isEmpty(user.getName()))
       {
           SearchQuery searchQuery = new NativeSearchQueryBuilder()
                   .withIndices("test_user")
                   .withQuery(matchQuery("name", user.getName()))
                   .build().setPageable(page);
           queryList.add(searchQuery);
         Long  count=elasticsearchTemplate.count(searchQuery);
       }


        List<List<TestUser>> staticCodeResult = elasticsearchTemplate.queryForList( queryList, TestUser.class);

        response=new ServiceResponse(HttpStatus.OK, StatusCode.SUCCESS,
                staticCodeResult, Collections.EMPTY_LIST);
         */

        //dynamic query building and pagination
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (user.getId() != null && user.getId() > 0L) {
            query.must(QueryBuilders.termQuery("id", user.getId()));
        }
        if (!Util.isEmpty(user.getName())) {
            query.must(QueryBuilders.termQuery("name", user.getName()));
        }
        if (user.getUserSettings() != null && !Util.isEmpty(user.getUserSettings().get("gender"))) {
            query.must(QueryBuilders.termQuery("userSettings.gender", user.getUserSettings().get("gender")));
        }

        //TODO: need to check for list data search
       /* if(user.getTestProject()!=null)
        {
            if(!Util.isEmpty(user.getTestProject().getName()))
            {
                query.must(QueryBuilders.termQuery("projectList.name", user.getTestProject().getName()));
            }

            if(user.getTestProject().getId() >0)
                query.must(QueryBuilders.termQuery("projectList.id", user.getTestProject().getId()));
        }*/

        response = new ServiceResponseExtended();
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("test_user")
                .withQuery(query)
                .build().setPageable(page);

        com.tigerit.soa.model.es.SearchResult result = elasticsearchTemplate.query(searchQuery, new ResultsExtractor<com.tigerit.soa.model.es.SearchResult>() {

            @Override
            public com.tigerit.soa.model.es.SearchResult extract(SearchResponse response) {
                long totalHits = response.getHits().getTotalHits();
                logger.info("totalHits:" + totalHits);
                List<TestUser> userList = new ArrayList<TestUser>();
                response.getHits().forEach(hit -> userList.add(JSON.parseObject(hit.getSourceAsString(), (Type) TestUser.class)));
               /* for (SearchHit hit : response.getHits()) {
                    if (hit != null) {
                        userList.add(JSON.par);
                    }
                }*/

                return new com.tigerit.soa.model.es.SearchResult(totalHits, userList);
            }
        });

        //TODO://  response.: set response values
        logger.info("totalHit: {} , userlist:", result.getTotalHit());
        response.setTotalHits(result.getTotalHit());
        response.setBody(result.getResultList());
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);

        return ResponseEntity.ok(response);

    }
}
