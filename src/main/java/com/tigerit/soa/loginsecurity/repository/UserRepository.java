package com.tigerit.soa.loginsecurity.repository;

//import com.tigerit.soa.loginsecurity.entity.User;
import com.tigerit.soa.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> ,UserRepositoryCustom {
	Optional<UserEntity> findByUsername(String username);

	//Boolean existsByUsername(String username);

	//Boolean existsByEmail(String email);

	Integer countById(Long id);

	Integer countByIdAndOrganizationId(Long id, BigInteger organizationId);

	//for user registration
	Optional<UserEntity> findByEmail(String email);

	UserEntity findByIdAndUsername(Long id, String userName);

	//Optional<UserEntity> findByUsernameAndCategory(String username, UserCategory userCategory);

	//Long countByUsernameInAndCategoryAndStatus(List<String> username, UserCategory userCategory, Status status);

	//Page<UserEntity> findAllByCategoryOrderByUsernameAsc(UserCategory category, Pageable pageable);
	Page<UserEntity> findAllByOrderByUsernameAsc(/*UserCategory category,*/ Pageable pageable);

	//Page<UserEntity> findAllByCategoryAndUsernameContainingIgnoreCaseOrderByUsernameAsc(/*UserCategory userCategory,*/ String username, Pageable pageable);
	Page<UserEntity> findAllByUsernameContainingIgnoreCaseOrderByUsernameAsc(/*UserCategory userCategory,*/ String username, Pageable pageable);

	/*@Query(value = "select u.* from users u where u.user_category = ?1 " +
			"and u.username in (select pu.user_name from partner_user pu " +
			" where pu.partner_id = ?2 and pu.user_name ilike %?3%) order by lower(u.username)", nativeQuery = true)
	Page<UserEntity> findAllSubUserByPartnerIdAndUsernameIgnoreCaseOrderByUsernameAsc(String userCategory, Long partnerId,
																				String username, Pageable pageable);
	*/
	/*@Query(value = "select u.* from users_actr u where u.user_category = ?1 " +
			"and u.organization_id = ?2 "+
			"and u.username ilike %?3% "+
			"order by lower(u.username) ", nativeQuery = true)
	*/
	@Query(value = "select u.* from users_actr u where " +
			" u.organization_id = ?1 "+
			"and u.username ilike %?2% "+
			"order by lower(u.username) ", nativeQuery = true)
	Page<UserEntity> findAllUserByOrganizationIdAndUsernameIgnoreCaseOrderByUsernameAsc(/*String userCategory,*/ Long organizationId,
																					  String username, Pageable pageable);


	/*@Query(value = " select u.* " +
			" from partners p, partner_user pu, users u " +
			" where pu.partner_id = p.id and pu.user_name = u.username and " +
			" p.id = ?1 and u.user_category = ?2 ", nativeQuery = true
	)
	List<UserEntity> findUserOfPartnerOrPartnersSubUser(Long partnerId, String userCategory);
	*/


}
/*@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}*/
