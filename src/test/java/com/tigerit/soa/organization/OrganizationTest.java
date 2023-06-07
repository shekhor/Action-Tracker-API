/*
package com.tigerit.soa.organization;

import com.tigerit.soa.entity.es.OrganizationEntity;
import com.tigerit.soa.repository.es.OrganizationRepository;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.service.OrganizationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@javax.transaction.Transactional
public class OrganizationTest {

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    OrganizationService organizationService;

    @Test
    public void testGetByIdRepository() {
        Optional<OrganizationEntity> entity = organizationRepository.findById(BigInteger.valueOf(2));

        Assert.assertTrue(entity.isPresent());
        Assert.assertNotNull(entity);
    }

    @Test
    public void testGetOrganizationById() {
        ServiceResponse response = organizationService.getOrganizationById(BigInteger.valueOf(2), "Fahim");

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), HttpStatus.OK);
        Assert.assertNotNull(response.getBody());
    }
}
*/
