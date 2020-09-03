package com.fsm.repositories;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fsm.models.ClientCompany;

@Repository
public interface ClientCompanyRepository extends JpaRepository<ClientCompany, Long> {

	@Query(value = "SELECT company_id FROM client_company WHERE LOWER(company_name) = LOWER(TRIM(:companyName,' ')) AND is_deleted = false limit 1", nativeQuery = true)
	Long findIdByName(@Param("companyName") String companyName);

	@Query(value = "SELECT * FROM client_company ORDER BY company_id DESC limit 1", nativeQuery = true)
	ClientCompany findIdByLastAdded();

	@Query(value = "SELECT * FROM client_company WHERE is_deleted = false AND (LOWER(company_name) LIKE LOWER(concat('%', :search ,'%')) OR CAST(company_id AS VARCHAR) LIKE concat('%', :search ,'%'))", nativeQuery = true)
	ArrayList<ClientCompany> getSearchByName(@Param("search") String search, Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM client_company WHERE is_deleted = false AND (LOWER(company_name) LIKE LOWER(concat('%', :search ,'%')) OR CAST(company_id AS VARCHAR) LIKE concat('%', :search ,'%'))", nativeQuery = true)
	Integer getTotalByName(@Param("search") String search);

	@Query(value = "SELECT * FROM client_company WHERE is_deleted = false", nativeQuery = true)
	ArrayList<ClientCompany> getAll();

	@Query(value = "SELECT * FROM client_company a JOIN client_company_branch b ON a.company_id = b.company_id WHERE LOWER(a.company_name) = BTRIM(LOWER(:companyName),' ') AND LOWER(b.branch_name) = BTRIM(LOWER(:companyName),' ') AND a.is_deleted = false LIMIT 1", nativeQuery = true)
	ClientCompany checkDupClientCompany(@Param("companyName") String companyName);

	@Query(value = "SELECT * FROM client_company WHERE LOWER(company_email) = BTRIM(LOWER(:companyEmail),' ') AND is_deleted = false", nativeQuery = true)
	ClientCompany checkDupEmailClientCompany(@Param("companyEmail") String companyEmail);
}
