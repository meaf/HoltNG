package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.SystemProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SysPropertyRepository extends IRepository<SystemProperty> {

  @Query("SELECT p FROM SystemProperty p where p.name = :name")
  SystemProperty getProperty(@Param("name") String  name);


}
