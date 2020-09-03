package com.fsm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import com.fsm.models.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long>{

    @Query(value = "SELECT * FROM user_group WHERE is_deleted = false", nativeQuery = true)
    ArrayList<UserGroup> listUserGroup();

}
