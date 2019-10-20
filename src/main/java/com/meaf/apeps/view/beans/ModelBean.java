package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.model.repository.ModelRepository;
import com.meaf.apeps.model.repository.WeatherStateRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.rmi.NoSuchObjectException;
import java.util.List;

@Component
@SessionScope
public class ModelBean {

  private final WeatherStateRepository weatherStateRepository;
  private final ModelRepository modelRepository;

  private Model model;

  public ModelBean(WeatherStateRepository weatherStateRepository, ModelRepository modelRepository) {
    this.weatherStateRepository = weatherStateRepository;
    this.modelRepository = modelRepository;
  }

  public Model getModel() {
    return this.model;
  }

  public void switchModel(Long modelId) throws NoSuchObjectException {
    this.model = modelRepository.findById(modelId).orElseThrow(() -> new NoSuchObjectException("Cannot find model by id=" + modelId));
  }

  public List<WeatherStateData> getEntries() {
    return weatherStateRepository.findDataByModelId(model.getId());
  }

  public void saveModel() {
    modelRepository.saveAndFlush(model);
  }


}
