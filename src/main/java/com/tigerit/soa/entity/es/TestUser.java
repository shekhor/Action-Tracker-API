package com.tigerit.soa.entity.es;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.*;

/**
 * Created by DIPU on 4/20/20
 */

@Data
@Document(indexName = "test_user", type = "test_data")
public class TestUser {
    @Id
    private Long id;
    private String name;
    private int age;
    private Date creationDate = new Date();

    @Field(type = FieldType.Nested)
    List<TestProject> testProjectList =new ArrayList<>();
    private Map<String, String> userSettings = new HashMap<>();

}
