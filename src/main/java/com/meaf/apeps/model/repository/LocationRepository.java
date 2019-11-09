package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends IRepository<Location> {

  @Query("SELECT l FROM Location l where l.id = :id")
  Location getById(@Param("id") Long id);

  @Query("SELECT m.location FROM Model m where m.projectId = :projectId")
  List<Location> listProjectLocation(Long projectId);
}
