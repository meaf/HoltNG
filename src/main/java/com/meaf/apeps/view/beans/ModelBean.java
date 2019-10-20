package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.DataEntry;
import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.repository.DataEntryRepository;
import com.meaf.apeps.model.repository.ModelRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.rmi.NoSuchObjectException;
import java.util.List;

@Component
@SessionScope
public class ModelBean {

  private final DataEntryRepository entryRepository;
  private final ModelRepository modelRepository;

  private Model model;

  public ModelBean(DataEntryRepository entryRepository, ModelRepository modelRepository) {
    this.entryRepository = entryRepository;
    this.modelRepository = modelRepository;
  }

  public Model getModel() {
    return this.model;
  }

  public void switchModel(Long modelId) throws NoSuchObjectException {
    this.model = modelRepository.findById(modelId).orElseThrow(() -> new NoSuchObjectException("Cannot find model by id=" + modelId));
  }

  public List<DataEntry> getEntries() {
    return entryRepository.findDataEntriesByModelId(model.getId());
  }

  public void saveModel() {
    modelRepository.saveAndFlush(model);
  }


}
