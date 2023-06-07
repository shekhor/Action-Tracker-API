package com.tigerit.soa.loginsecurity.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 *
 */
@Component
public class UserRepositoryImpl implements UserRepositoryCustom {

    Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @PersistenceContext(unitName = "postgresql")
    private EntityManager entityManager;

//    public UserRepositoryImpl(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }

    @Override
    public List<String> findMethodAccessListByUsername(String username) {
        String sql = "SELECT p.privilege_name " +
                "FROM privilege_actr p " +
                "JOIN role_privilege_mapping_actr rpm ON ( p.id = rpm.privilege_id ) " +
                "JOIN roles_actr ractr ON ( rpm.role_id = ractr.id ) " +
                "JOIN users_actr uactr ON ( ractr.id = uactr.user_role_id ) " +
                "WHERE p.status = 'ACTIVE' "+
                "and uactr.username = ? ";

        Query query = entityManager.createNativeQuery(sql).setParameter(1, username);
        List<String> methodAccessList =(List<String> ) query.getResultList();
        return methodAccessList;
    }
}