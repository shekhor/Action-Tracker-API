package com.tigerit.soa.repository;

import com.tigerit.soa.entity.RiskLookupEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface RiskLookupRepository extends CrudRepository<RiskLookupEntity, BigInteger>{

    List<RiskLookupEntity> findAllByStatus(String status);
}
