package com.meaf.apeps.view.beans;

import com.meaf.apeps.model.entity.Location;
import com.meaf.apeps.model.repository.LocationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class LocationBean  {
private final LocationRepository locationRepository;

  private Location location;

  public LocationBean(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setLocation(Long locationId){
    this.location = locationRepository.getById(locationId);
  }

  public Location getCurrentLocation(){
    return this.location;
  }

}
