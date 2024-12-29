package com.alexaytov.ai_hub.controllers;

import com.alexaytov.ai_hub.model.DataSourceProjection;
import com.alexaytov.ai_hub.model.dtos.DataDto;
import com.alexaytov.ai_hub.model.entities.DataSource;
import com.alexaytov.ai_hub.services.DataSourceService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataSourceController {

  private final DataSourceService dataSourceService;

  public DataSourceController(DataSourceService dataSourceService) {
    this.dataSourceService = dataSourceService;
  }

  @PostMapping("/data-sources")
  public ResponseEntity<DataSource> saveDataSource(@RequestBody DataDto dto) {
    return ResponseEntity.ok(dataSourceService.saveDataSource(dto));
  }

  @DeleteMapping("/data-sources/{id}")
  public ResponseEntity<Void> deleteDataSource(@PathVariable Long id) {
    dataSourceService.deleteDataSource(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/data-sources")
  public ResponseEntity<List<DataSourceProjection>> getDataSources() {
    return ResponseEntity.ok(dataSourceService.getDataSources());
  }

}
