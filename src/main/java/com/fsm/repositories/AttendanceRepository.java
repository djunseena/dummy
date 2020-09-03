package com.fsm.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fsm.models.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	
	@Query(value = "SELECT * FROM attendance WHERE user_id = :userId ORDER BY attendance_id DESC LIMIT 1", nativeQuery = true)
	Attendance findCheckinAttendanceByUser(@Param("userId") Long userId);
	
	@Query(value = "SELECT * FROM attendance WHERE user_id = :userId AND date(check_in) = :date AND is_deleted = false ORDER BY attendance_id DESC LIMIT 1", nativeQuery = true)
	Attendance checkUserCheckin(@Param("userId") Long userId, @Param("date") Date date);
}
