package com.fsm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.ArrayList;
import com.fsm.models.ClientCompanyPIC;

public interface ClientCompanyPICRepository extends JpaRepository<ClientCompanyPIC, Long> {

	@Query(value = "SELECT pic_id FROM client_company_pic WHERE LOWER(pic_name) = LOWER(TRIM(:picName,' ')) AND branch_id = :branchId limit 1", nativeQuery = true)
	Long findIdByBranch(@Param("branchId") Long branchId, @Param("picName") String picName);

	@Query(value = "SELECT * FROM client_company_pic WHERE branch_id = :branchId AND is_deleted = false", nativeQuery = true)
	ArrayList<ClientCompanyPIC> findIdByBranchId(@Param("branchId") Long branchId);

	@Query(value = "SELECT pic_name FROM client_company_pic WHERE branch_id = :branchId limit 1", nativeQuery = true)
	String getByBranchId(@Param("branchId") Long branchId);

	@Query(value = "SELECT * FROM client_company_pic WHERE company_id = :companyId limit 1", nativeQuery = true)
	ClientCompanyPIC getByCompanyId(@Param("companyId") Long companyId);

	@Query(value = "SELECT * FROM client_company_pic WHERE is_deleted = false", nativeQuery = true)
	ArrayList<ClientCompanyPIC> findAllCPics();

	@Query(value = "SELECT * FROM client_company_pic WHERE branch_id = :branchId AND is_deleted = false", nativeQuery = true)
	ClientCompanyPIC getPICByBranchId(@Param("branchId") Long branchId);

	@Query(value = "SELECT * FROM client_company_pic WHERE company_id = :companyId AND is_deleted = false limit 1", nativeQuery = true)
	ClientCompanyPIC getPICByCompanyId(@Param("companyId") Long companyId);

	@Query(value = "SELECT * FROM client_company_pic WHERE company_id = :companyId", nativeQuery = true)
	ArrayList<ClientCompanyPIC> getAllPICByCompanyId(@Param("companyId") Long companyId);

}
