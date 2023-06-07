package com.tigerit.soa.request.es;

import com.tigerit.soa.entity.es.TestProject;
import lombok.Data;

import java.util.*;

/**
 * Created by DIPU on 4/23/20
 */
@Data
public class TestUserRequest {

    private Long id;
    private String name;
    private int age;
    private Date creationDate = new Date();

    private TestProject testProject;
    private Map<String, String> userSettings = new HashMap<>();
}
