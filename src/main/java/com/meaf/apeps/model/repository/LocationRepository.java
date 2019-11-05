package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationRepository extends IRepository<Location> {

  @Query("SELECT k FROM Location k where k.id = :id")
  Location getById(@Param("id") Long id);
}
