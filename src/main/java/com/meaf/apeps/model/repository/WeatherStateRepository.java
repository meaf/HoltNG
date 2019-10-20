package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.WeatherStateData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeatherStateRepository extends IRepository<WeatherStateData> {

  @Query("SELECT d FROM WeatherStateData d where d.modelId = :modelId order by date asc")
  List<WeatherStateData> findDataByModelId(@Param("modelId") Long modelId);

}
