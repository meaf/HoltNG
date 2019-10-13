package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.DataEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataEntryRepository extends IRepository<DataEntry> {

  @Query("SELECT d FROM DataEntry d where d.modelId = :modelId order by date asc")
  List<DataEntry> findDataEntriesByModelId(@Param("modelId") Long modelId);

}
