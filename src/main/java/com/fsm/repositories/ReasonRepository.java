package com.fsm.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.Reason;

@Repository
public interface ReasonRepository extends JpaRepository<Reason, Long> {

//	Query untuk LOV Hold reason
	@Query(value = "SELECT * FROM reason WHERE reason_type_id = 40 AND reason_desc ~* :reasonDesc", nativeQuery = true)
	public Slice<Reason> getHoldReason(@Param("reasonDesc") String reasonDesc, Pageable pageable);
	
//	Query untuk LOV cancel reason
	@Query(value = "SELECT * FROM reason WHERE reason_type_id = 41 AND reason_desc ~* :reasonDesc", nativeQuery = true)
	public Slice<Reason> getCancelReason(@Param("reasonDesc") String reasonDesc, Pageable pageable);
	
//	Query untuk update order detail hold reason (code :40)
	@Query(value = "SELECT * FROM reason WHERE reason_type_id = 40", nativeQuery = true)
	public List<Reason> getHold();
	
//	Query untuk update order detail cancel reason (code :41)
	@Query(value = "SELECT * FROM reason WHERE reason_type_id = 41", nativeQuery = true)
	public List<Reason> getCancel();
}
