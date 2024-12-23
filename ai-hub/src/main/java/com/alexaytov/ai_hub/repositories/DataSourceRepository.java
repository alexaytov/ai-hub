package com.alexaytov.ai_hub.repositories;

import com.alexaytov.ai_hub.model.DataSourceProjection;
import com.alexaytov.ai_hub.model.entities.DataSource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceRepository extends JpaRepository<DataSource, Long> {

  List<DataSourceProjection> findAllByUserId(Long userId);

  void deleteByIdAndUserId(Long id, Long userId);

}
