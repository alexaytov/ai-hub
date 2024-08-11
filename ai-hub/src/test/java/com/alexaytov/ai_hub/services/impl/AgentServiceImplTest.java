package com.alexaytov.ai_hub.services.impl;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.alexaytov.ai_hub.model.dtos.AgentDto;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.model.entities.Agent;
import com.alexaytov.ai_hub.model.entities.SystemMessage;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.AgentRepository;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.repositories.SystemMessageRepository;
import com.alexaytov.ai_hub.services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AgentServiceImplTest {

    private UserService userService;
    private AgentRepository agentRepository;
    private ModelMapper modelMapper;
    private SystemMessageRepository systemMessageRepository;
    private ModelRepository modelRepository;

    private AgentServiceImpl classUnderTest;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        agentRepository = mock(AgentRepository.class);
        modelMapper = mock(ModelMapper.class);
        systemMessageRepository = mock(SystemMessageRepository.class);
        modelRepository = mock(ModelRepository.class);

        classUnderTest = new AgentServiceImpl(userService, agentRepository, modelMapper, systemMessageRepository, modelRepository);
    }

    @Test
    void whenGettingAgents_thenCorrectValueIsReturned() {
        User user = new User();
        user.setId(1L);

        AIModel model = new AIModel();
        model.setId(1L);

        SystemMessage message = new SystemMessage();
        message.setId(1L);

        Agent first = new Agent();
        first.setId(1L);
        first.setModel(model);
        first.setSystemMessage(message);
        Agent second = new Agent();
        second.setId(2L);
        second.setModel(model);
        second.setSystemMessage(message);
        user.setAgents(List.of(first, second));

        when(userService.getUser()).thenReturn(user);
        when(modelMapper.map(first, AgentDto.class)).thenReturn(new AgentDto());
        when(modelMapper.map(second, AgentDto.class)).thenReturn(new AgentDto());

        List<AgentDto> agents = classUnderTest.getAgents();
        assertEquals(2, agents.size());

        AgentDto firstActual = agents.get(0);
        assertEquals(first.getId(), firstActual.getId());
        AgentDto secondActual = agents.get(1);
        assertEquals(second.getId(), secondActual.getId());
    }

    @Test
    void givenExistingAgent_whenGettingAgent_thenCorrectValueIsReturned() {
        User user = new User();
        user.setId(1L);

        Agent agent = new Agent();
        agent.setUser(user);

        AIModel model = new AIModel();
        model.setId(1L);
        agent.setModel(model);

        SystemMessage message = new SystemMessage();
        message.setId(1L);
        agent.setSystemMessage(message);

        when(userService.getUser()).thenReturn(user);
        when(agentRepository.findById(1L)).thenReturn(java.util.Optional.of(agent));

        AgentDto agentDto = classUnderTest.getAgent(1L);
        assertEquals(agent.getId(), agentDto.getId());
    }

    @Test
    void givenExistingAgent_whenDeletingAgent_thenCorrectAgentIsDeleted() {
        User user = new User();
        user.setId(1L);

        Agent agent = new Agent();
        agent.setUser(user);

        when(userService.getUser()).thenReturn(user);
        when(agentRepository.findById(1L)).thenReturn(java.util.Optional.of(agent));

        classUnderTest.deleteAgent(1L);

        verify(agentRepository).delete(agent);
    }

    @Test
    void givenNonExistingAgent_whenDeletingAgent_thenNoAgentIsDeleted() {
        User user = new User();
        user.setId(1L);

        when(userService.getUser()).thenReturn(user);
        when(agentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        classUnderTest.deleteAgent(1L);

        verify(agentRepository, never()).delete(any());
    }

    @Test
    void whenCreatingAgent_thenCorrectAgentIsCreated() {
        User user = new User();
        user.setId(1L);

        SystemMessage message = new SystemMessage();
        message.setId(1L);
        message.setUser(user);

        when(systemMessageRepository.findById(1L)).thenReturn(java.util.Optional.of(message));
        AIModel model = new AIModel();
        model.setId(1L);

        when(userService.getUser()).thenReturn(user);
        when(modelRepository.findById(1L)).thenReturn(java.util.Optional.of(model));

        Agent agent = new Agent();
        agent.setId(1L);

        AgentDto agentDto = new AgentDto();
        agentDto.setId(1L);
        agentDto.setSystemMessageId(1L);
        agentDto.setModelId(1L);
        when(modelMapper.map(agentDto, Agent.class)).thenReturn(agent);

        when(agentRepository.save(agent)).thenReturn(agent);

        AgentDto actual = classUnderTest.createAgent(agentDto);
        assertEquals(agentDto.getId(), actual.getId());
    }

}