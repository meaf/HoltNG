package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.Model;
import com.meaf.apeps.model.entity.User;
import com.meaf.apeps.model.entity.WeatherStateData;
import com.meaf.apeps.model.repository.ModelRepository;
import com.meaf.apeps.model.repository.UserModelManagementRepository;
import com.meaf.apeps.model.repository.WeatherStateRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Component
@SessionScope
public class ModelBean {

  private final WeatherStateRepository weatherStateRepository;
  private final ModelRepository modelRepository;
  private final UserModelManagementRepository userModelManagementRepository;
  private final SessionBean sessionBean;

  private Model model;

  public ModelBean(WeatherStateRepository weatherStateRepository, ModelRepository modelRepository, UserModelManagementRepository userModelManagementRepository, SessionBean sessionBean) {
    this.weatherStateRepository = weatherStateRepository;
    this.modelRepository = modelRepository;
    this.userModelManagementRepository = userModelManagementRepository;
    this.sessionBean = sessionBean;
  }

  public Model getModel() {
    return this.model;
  }

  public void switchModel(Long modelId) {
    this.model = modelRepository.findById(modelId).orElse(null);
  }

  public List<WeatherStateData> getEntries() {
    return weatherStateRepository.findDataByModelId(model.getId());
  }

  public void saveCurrentModel() {
    modelRepository.saveAndFlush(model);
  }

  public Model save(Model model){
    return modelRepository.saveAndFlush(model);
  }

  public void mergeEntries(List<WeatherStateData> data) {
    weatherStateRepository.saveAll(data);
  }


  public boolean isUserManager(Model model) {
    User user = sessionBean.getLoggedInUser();
    if (user == null)
      return false;

    return user.getAdmin() || userModelManagementRepository.canUserManage(model.getId(), user.getId()) != null;
  }

  public void removeItems(Iterable<WeatherStateData> stateData) {
    weatherStateRepository.deleteAll(stateData);
  }
}
