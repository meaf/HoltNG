package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.LocationKey;
import com.meaf.apeps.model.repository.LocationKeyRepository;
import com.meaf.apeps.model.repository.SysPropertyRepository;
import com.meaf.apeps.utils.HoltConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Component
@SessionScope
public class PropertiesBean {
  private final SysPropertyRepository sysPropertyRepository;
  private final LocationKeyRepository locationKeyRepository;

  public PropertiesBean(SysPropertyRepository sysPropertyRepository, LocationKeyRepository locationKeyRepository) {
    this.sysPropertyRepository = sysPropertyRepository;
    this.locationKeyRepository = locationKeyRepository;
  }

  public List<LocationKey> getLocationKeys(Long locationId){
    return locationKeyRepository.findKeysForLocation(locationId);
  }

  public String getMapsKey(){
    return sysPropertyRepository.getProperty(HoltConstants.GCP_MAPS_API_KEY).getValue();
  }


}
