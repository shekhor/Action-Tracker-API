package com.tigerit.soa.loginsecurity.repository;

import com.tigerit.soa.entity.PrivilegeEntity;
import com.tigerit.soa.loginsecurity.util.core.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    //List<PrivilegeEntity> findAllByStatus(Status status);

    /*@Query("SELECT NEW com.tigerit.nid2.partnerservice.model.bean.SlaInfoBean (sla.id, sla.name, sla.methodGroup.id, sla.status)" +
            " FROM ServiceLayerAgreement sla JOIN com.tigerit.nid2.partnerservice.entity.db.PartnerSla ps" +
            " ON (ps.slaId = sla.id) where ps.partnerId=?1")
    List<SlaInfoBean> findAllByPartnerId(Long partnerId);
    */
    //List<ServiceLayerAgreement> findByIdIn(List<Long> ids);

    /*@Query("SELECT NEW com.tigerit.nid2.partnerservice.model.bean.SlaBean (sla.id, sla.requestSettings, sla.responseSettings) " +
            "FROM ServiceLayerAgreement sla " +
            "LEFT JOIN UsersGroup ug on (sla.methodGroup.id = ug.groupId) where ug.username=?1")
    List<SlaBean> fetchSlaLeftJoinUserGroup(String username);
*/
  //  List<ServiceLayerAgreement> findByNameAndIdNot(String name, Long Id);

    //List<ServiceLayerAgreement> findByName(String name);

    //Page<ServiceLayerAgreement> findAllByStatus(Status status, Pageable pageable);

    //Page<ServiceLayerAgreement> findByStatusAndNameContainingIgnoreCase(Status status, String name, Pageable pageable);

    //Long countByStatusAndNameContainingIgnoreCase(Status status, String name);

    //Long countAllByStatus(Status status);
}
