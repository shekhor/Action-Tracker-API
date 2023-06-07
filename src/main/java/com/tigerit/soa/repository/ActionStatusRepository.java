package com.tigerit.soa.repository;

import com.tigerit.soa.entity.ActionStatusEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ActionStatusRepository extends CrudRepository<ActionStatusEntity, BigInteger> {

    List<ActionStatusEntity> findAllByStatus(String status);
}
