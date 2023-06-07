package com.tigerit.soa.entity.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

/*
Fahim created at 4/19/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(indexName = "test_object_4", type = "_doc")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestEsEntity2 {
    @Id
    private String id;
    private String firstName;
    private String email;

    @Field(type = FieldType.Nested)
    private List<TestProject> testProject;

    @Field(type = FieldType.Object)
    private Map<String, String> json_field;
}
