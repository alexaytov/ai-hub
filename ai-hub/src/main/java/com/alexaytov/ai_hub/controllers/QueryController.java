package com.alexaytov.ai_hub.controllers;

import com.alexaytov.ai_hub.model.dtos.ChatMessageDto;
import com.alexaytov.ai_hub.model.dtos.QueryRequestDto;
import com.alexaytov.ai_hub.services.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController {

  private final QueryService queryService;

  public QueryController(QueryService queryService) {
    this.queryService = queryService;
  }

  @PostMapping("/query")
  public ResponseEntity<ChatMessageDto> query(@RequestBody QueryRequestDto request) {
    return ResponseEntity.status(201).body(queryService.query(request));
  }

}
