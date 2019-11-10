package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.UserModelManagement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserModelManagementRepository extends IRepository<UserModelManagement> {

  @Query("SELECT m FROM UserModelManagement m where m.modelId = :modelId and m.userId = :userId")
  UserModelManagement canUserManage(@Param("modelId") Long modelId, @Param("userId") Long userId);


}
