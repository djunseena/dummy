package com.fsm.repositories;

import java.util.ArrayList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fsm.models.Action;

@Repository
public interface ActionRepository extends PagingAndSortingRepository<Action, Long> {

	public static final String Get_Data_Action = "SELECT a.* FROM \"action\" as a\n" 
			+ "JOIN failure as f on a.failure_id = f.failure_id\n" 
			+ "WHERE a.is_deleted = FALSE AND (CAST(action_id as VARCHAR) LIKE concat('%',BTRIM(:search,' '),'%') OR\n" 
			+ "LOWER(a.action_desc) LIKE LOWER(concat('%', BTRIM(:search,' '),'%')))";
	
	@Query(value = "SELECT * FROM action WHERE failure_id = :failureId AND action_desc ~* :filter", nativeQuery = true)
	public Slice<Action> getActionByFailureId(@Param("failureId") Long failureId, Pageable pageable, String filter);

	@Query(value = "select * from action where failure_id = :failureId and is_deleted = false\n"
			+ "order by action_id limit 1", nativeQuery = true)
	Action checkFailure(@Param("failureId") Long failureId);
	
	@Query(value = "SELECT * FROM action\n" + 
			"WHERE LOWER(action_desc) = LOWER(TRIM(:actionDesc)) AND failure_id = :failureId AND is_deleted = FALSE", nativeQuery = true)
	ArrayList<Action> checkDataAlreadyExist (@Param("actionDesc")String actionDesc, @Param("failureId")Long failureId);
	
	@Query(value = "SELECT * FROM action\n" + 
			"WHERE LOWER(action_desc) = LOWER(TRIM(:actionDesc)) AND failure_id = :failureId AND is_deleted = FALSE AND action_id NOT IN (:actionId) ", nativeQuery = true)
	ArrayList<Action> checkDuplicateData (@Param("actionDesc")String actionDesc, @Param("failureId")Long failureId, @Param("actionId")Long actionId);
	
	@Query(value = "SELECT * FROM \"action\" WHERE is_deleted = FALSE", nativeQuery = true)
	ArrayList<Action> findAllAction();
	
	@Query(value = Get_Data_Action, nativeQuery = true)
	ArrayList<Action> getDataAction(@Param("search")String search, Pageable pageable);
}
