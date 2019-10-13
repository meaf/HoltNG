package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepository extends IRepository<User> {

  /* A version to fetch List instead of Page to avoid extra count query. */
  List<User> findAllBy(Pageable pageable);

  List<User> findByNameLikeIgnoreCase(String nameFilter);

  // For lazy loading and filtering
  List<User> findByNameLikeIgnoreCase(String nameFilter, Pageable pageable);

  long countByNameLikeIgnoreCase(String nameFilter);

}
