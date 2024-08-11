package com.alexaytov.ai_hub.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexaytov.ai_hub.model.dtos.AIModelDto;
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.ModelService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AIModelControllerTest {

    private ModelService service;
    private AuditLogService auditLog;
    private AIModelController classUnderTest;

    @BeforeEach
    void setup() {
        service = mock(ModelService.class);
        auditLog = mock(AuditLogService.class);
        classUnderTest = new AIModelController(auditLog, service);
    }

    @Test
    void givenModelExists_whenGettingModel_thenModelIsReturned() {
        var result = classUnderTest.getModel(1L);

        assertNotNull(result);
    }

    @Test
    void givenModelExists_whenDeletingModel_thenModelIsDeleted() {
        var result = classUnderTest.deleteModel(1L);

        assertNotNull(result);
        verify(service).deleteModel(1L);
        verify(auditLog).postAuditLog("Deleting model with id 1");
        verify(auditLog).postAuditLog("Model with id 1 was deleted");
    }

    @Test
    void whenCreatingModel_thenModelIsCreated() {
        AIModelDto dto = new AIModelDto();
        var result = classUnderTest.createModel(dto);

        assertNotNull(result);
        verify(service).createModel(dto);
        verify(auditLog).postAuditLog("Creating model with name null");
        verify(auditLog).postAuditLog("Model with name null and id null was created");
    }

    @Test
    void whenGettingModels_thenModelsAreReturned() {
        var result = classUnderTest.getModels();

        assertNotNull(result);
    }

    @Test
    void whenGettingModel_thenCorretModelIsReturned() {
        var result = classUnderTest.getModel(1L);

        assertNotNull(result);
        verify(service).getModel(1L);
    }

}