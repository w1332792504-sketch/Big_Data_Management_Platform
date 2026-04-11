package com.datamanager.repository;

import com.datamanager.model.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {
    List<DataSource> findByStatusOrderByCreateTimeDesc(Integer status);
    List<DataSource> findAllByOrderByCreateTimeDesc();
    boolean existsByName(String name);
}
