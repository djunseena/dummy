package com.fsm.repositories;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.Code;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

	@Query(value = "SELECT * FROM code WHERE category_code_id = :categoryCodeId", nativeQuery = true)
	public ArrayList<Code> findByCodeByCategoryCodeId(@Param("categoryCodeId") long codeId);

	@Query(value = "SELECT * FROM code WHERE code_id = :codeId", nativeQuery = true)
	public Code findByCodeId(@Param("codeId") long codeId);

	@Query(value = "SELECT * FROM code WHERE category_code_id = 10", nativeQuery = true)
	public ArrayList<Code> findByCodeUserIdentity();

	@Query(value = "SELECT * FROM code WHERE category_code_id = 5", nativeQuery = true)
	public ArrayList<Code> findByCodePriority();

	@Query(value = "SELECT * FROM code WHERE category_code_id = 13", nativeQuery = true)
	public ArrayList<Code> findByCodeReport();

	@Query(value = "SELECT * FROM code WHERE category_code_id = 6", nativeQuery = true)
	public ArrayList<Code> findByCodeCategory();

	@Query(value = "SELECT code_id FROM code WHERE LOWER(code_name) = LOWER(TRIM(:codeName,' ')) AND code_desc = 'Ticket Category' limit 1", nativeQuery = true)
	Long findIdByCodeNameTicketCategory(@Param("codeName") String codeName);

	@Query(value = "SELECT code_id FROM code WHERE LOWER(code_name) = LOWER(TRIM(:codeName,' ')) AND code_desc = 'Priority Status' limit 1", nativeQuery = true)
	Long findIdByCodeNamePriorityStatus(@Param("codeName") String codeName);

//	Query for Get User Identity use Filter and Pagination
	@Query(value = "SELECT * FROM code c WHERE category_code_id = 10 AND code_name ~* :userIdentity", nativeQuery = true)
	Slice<Code> getUserIdentityWithFilter(@Param("userIdentity") String userIdentity, Pageable pageable);
}