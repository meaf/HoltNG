package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends IRepository<Project> {

  @Query("select distinct p from Project p left join UserProjectRelation r on p.id = r.projectId where p.privateProject = 0 or r.userId = :userId or :admin = true")
  List<Project> findAvailableProject(@Param("userId") Long userId, @Param("admin") Boolean admin);
}
