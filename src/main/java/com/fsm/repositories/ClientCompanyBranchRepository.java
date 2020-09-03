package com.fsm.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import com.fsm.models.ClientCompanyBranch;

@Repository
public interface ClientCompanyBranchRepository extends JpaRepository<ClientCompanyBranch, Long> {

	@Query(value = "SELECT branch_id FROM client_company_branch WHERE company_id = :companyId AND LOWER(branch_name) = LOWER(TRIM(:branchName,' ')) limit 1", nativeQuery = true)
	Long findIdByName(@Param("companyId") Long companyId, @Param("branchName") String branchName);

	@Query(value = "SELECT * FROM client_company_branch WHERE company_id = :companyId AND is_deleted = false", nativeQuery = true)
	ArrayList<ClientCompanyBranch> getByCompanyId(@Param("companyId") Long companyId);

	@Query(value = "SELECT * FROM client_company_branch WHERE company_id = :companyId limit 1", nativeQuery = true)
	ClientCompanyBranch findByCompanyId(@Param("companyId") Long companyId);

	@Query(value = "SELECT * FROM client_company_branch ORDER BY branch_id DESC limit 1", nativeQuery = true)
	ClientCompanyBranch findByLastAdded();

	@Query(value = "SELECT * FROM client_company_branch WHERE is_deleted = false", nativeQuery = true)
	ArrayList<ClientCompanyBranch> findAllBranchs();

	@Query(value = "SELECT * FROM client_company_branch AS b JOIN client_company_pic AS p ON \n"
	+ " b.branch_id = p.branch_id JOIN client_company AS c ON b.company_id = c.company_id WHERE (b.is_deleted = false AND p.is_deleted = false) AND p.pic_type = 44 AND (LOWER(b.branch_name) LIKE LOWER(CONCAT('%',:search,'%')) \n"
	+ " OR LOWER(p.pic_name) LIKE LOWER(CONCAT('%',:search,'%')) OR CAST(b.branch_id AS VARCHAR) LIKE concat('%', :search ,'%')\n"
	+ " OR CAST(p.pic_id AS VARCHAR) LIKE concat('%', :search ,'%'))", nativeQuery = true)
	ArrayList<ClientCompanyBranch> getBranchList(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM client_company_branch AS b JOIN client_company_pic AS p ON \n"
	+ "b.branch_id = p.branch_id JOIN client_company AS c ON b.company_id = c.company_id WHERE (b.is_deleted = false AND p.is_deleted = false) AND (LOWER(b.branch_name) LIKE LOWER(CONCAT('%',:search,'%')) \n"
	+ "OR LOWER(p.pic_name) LIKE LOWER(CONCAT('%',:search,'%')) OR CAST(b.branch_id AS VARCHAR) LIKE concat('%', :search ,'%')\n"
	+ " OR CAST(p.pic_id AS VARCHAR) LIKE concat('%', :search ,'%'))", nativeQuery = true)
	Integer getTotalBranch(@Param("search") String search);

	@Query(value = "SELECT * FROM client_company_branch WHERE is_deleted = false", nativeQuery = true)
	ArrayList<ClientCompanyBranch> findAllBranchPagination(Pageable pageable);

	@Query(value = "SELECT * FROM client_company_branch WHERE company_id = :companyId AND is_deleted = false LIMIT 1", nativeQuery = true)
	ClientCompanyBranch getClientCompanyBranchByCompanyId(@Param("companyId") Long companyId);

	@Query(value = "SELECT * FROM client_company_branch a JOIN client_company_pic b ON a.branch_id = b.branch_id WHERE LOWER(a.branch_name) = BTRIM(LOWER(:branchName),' ') AND LOWER(b.pic_name) = BTRIM(LOWER(:picName),' ') AND a.is_deleted = false LIMIT 1", nativeQuery = true)
	ClientCompanyBranch checkDupClientCompanyBranch(@Param("branchName") String branchName, @Param("picName") String picName);
}
