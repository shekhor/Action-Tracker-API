/*
package com.tigerit.soa.organization;

import com.tigerit.soa.entity.es.OrganizationEntity;
import com.tigerit.soa.repository.es.OrganizationRepository;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.serviceImpl.OrganizationServiceImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.math.BigInteger;
import java.util.Optional;

import static org.mockito.Mockito.when;

*/
/*
Fahim created at 5/7/2020
*//*

@RunWith(MockitoJUnitRunner.class)
public class OrganizationMockTest {

    @Mock
    OrganizationRepository organizationRepository;

    @InjectMocks
    OrganizationServiceImpl organizationService;

    static OrganizationEntity entity = new OrganizationEntity();

    @BeforeClass
    public static void prepareTestObject() {
        entity.setId(BigInteger.valueOf(2));
    }

    @Test
    public void testGetOrganizationByIdMock() {

        when(organizationRepository.findById(BigInteger.valueOf(2))).thenReturn(Optional.of(entity));

        ServiceResponse response = organizationService.getOrganizationById(BigInteger.valueOf(2), "Fahim");

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), HttpStatus.OK);
        Assert.assertNotNull(response.getBody());
    }
}
*/
