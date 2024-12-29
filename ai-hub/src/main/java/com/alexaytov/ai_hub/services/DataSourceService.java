package com.alexaytov.ai_hub.services;

import com.alexaytov.ai_hub.model.DataSourceProjection;
import com.alexaytov.ai_hub.model.dtos.DataDto;
import com.alexaytov.ai_hub.model.entities.DataSource;
import java.util.List;

public interface DataSourceService {

  DataSource saveDataSource(DataDto dataSource);

  List<DataSourceProjection> getDataSources();

  void deleteDataSource(Long id);
}
