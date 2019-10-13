package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.Model;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModelRepository extends IRepository<Model> {

  @Query("SELECT m FROM Model m where m.projectId = :projectId")
  List<Model> findModelsByProjectId(@Param("projectId") Long projectId);

}
