package com.meaf.apeps.model;

import org.springframework.data.jpa.repository.JpaRepository;

interface IRepository<T extends ABaseEntity> extends JpaRepository<T, Long> {
}
