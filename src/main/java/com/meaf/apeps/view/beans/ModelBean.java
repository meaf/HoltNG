package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.DataEntry;
import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.repository.DataEntryRepository;
import com.meaf.apeps.model.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Component
@SessionScope
public class ModelBean {

  @Autowired
  DataEntryRepository entryRepository;
  @Autowired
  ModelRepository modelRepository;

  private Model model;

  public Model getModel(){
    return this.model;
  }

  public void switchModel(Long modelId){
    this.model = modelRepository.findById(modelId).get();
  }

  public List<DataEntry> getEntries() {
    return entryRepository.findDataEntriesByModelId(model.getId());
  }


}
