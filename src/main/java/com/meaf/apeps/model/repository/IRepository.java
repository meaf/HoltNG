package com.meaf.apeps.model.repository;

import com.meaf.apeps.model.entity.ABaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface IRepository<T extends ABaseEntity> extends JpaRepository<T, Long> {
}
