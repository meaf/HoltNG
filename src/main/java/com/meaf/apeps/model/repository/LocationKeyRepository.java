package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.LocationKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationKeyRepository extends IRepository<LocationKey> {

  @Query("SELECT k FROM LocationKey k where k.locationId = :locationId")
  List<LocationKey> findKeysForLocation(@Param("locationId") Long locationId);

}
