package com.fsm.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import com.fsm.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "SELECT * FROM role WHERE LOWER(role_name) LIKE BTRIM(LOWER(:role_name),' ') AND user_group_id = :user_group_id AND is_deleted = false LIMIT 1", nativeQuery = true)
	Role findRoleByRole(@Param("role_name") String roleName, @Param("user_group_id") Long userGroupId);

    @Query(value = "SELECT * FROM role WHERE is_deleted = false AND \n"
    + " (CAST(role_id AS VARCHAR) LIKE CONCAT('%', :search , '%') OR \n"
    + " LOWER(role_name) LIKE LOWER(CONCAT('%', :search,'%')))", nativeQuery = true)
    ArrayList<Role> getListRole (@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM role WHERE is_deleted = false AND \n"
    + " (CAST(role_id AS VARCHAR) LIKE CONCAT('%', :search , '%') OR \n"
    + " LOWER(role_name) LIKE LOWER(CONCAT('%', :search,'%')))", nativeQuery = true)
    Integer getTotalListRole (@Param("search") String search);
}
