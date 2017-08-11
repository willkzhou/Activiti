/*
 * Copyright 2017 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.services.query.events.handlers;

import org.activiti.services.query.app.model.Variable;
import org.activiti.services.query.events.ProcessEngineEvent;
import org.activiti.services.query.events.VariableCreatedEvent;
import org.activiti.test.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class VariableCreatedEventHandlerTest {

    @InjectMocks
    private VariableCreatedEventHandler handler;

    @Mock
    private TaskVariableCreatedHandler taskVariableCreatedHandler;

    @Mock
    private ProcessVariableCreatedHandler processVariableCreatedHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void handleShouldCreateUserProcessInstanceVariableCreatedHandlerWhenNoTaskId() throws Exception {
        //given
        String executionId = "10";
        long processInstanceId = 30L;
        String variableName = "var";
        String variableType = String.class.getName();
        VariableCreatedEvent event = new VariableCreatedEvent(System.currentTimeMillis(),
                                                              "variableCreated",
                                                              executionId,
                                                              "20",
                                                              String.valueOf(processInstanceId),
                                                              variableName,
                                                              "content",
                                                              variableType,
                                                              null);

        //when
        handler.handle(event);

        //then
        ArgumentCaptor<Variable> captor = ArgumentCaptor.forClass(Variable.class);
        verify(processVariableCreatedHandler).handle(captor.capture());

        Variable variable = captor.getValue();
        Assertions.assertThat(variable)
                .hasExecutionId(executionId)
                .hasProcessInstanceId(String.valueOf(processInstanceId))
                .hasName(variableName)
                .hasTaskId(null)
                .hasType(variableType);
    }

    @Test
    public void handleShouldUseTaskVariableCreatedHandlerWhenTaskIdIsProvided() throws Exception {
        //given
        String executionId = "10";
        String processInstanceId = "30";
        String variableName = "var";
        String variableType = String.class.getName();
        String taskId = "40";
        VariableCreatedEvent event = new VariableCreatedEvent(System.currentTimeMillis(),
                                                              "variableCreated",
                                                              executionId,
                                                              "20",
                                                              processInstanceId,
                                                              variableName,
                                                              "content",
                                                              variableType,
                                                              taskId);

        //when
        handler.handle(event);

        //then
        ArgumentCaptor<Variable> captor = ArgumentCaptor.forClass(Variable.class);
        verify(taskVariableCreatedHandler).handle(captor.capture());

        Variable variable = captor.getValue();
        Assertions.assertThat(variable)
                .hasExecutionId(executionId)
                .hasProcessInstanceId(processInstanceId)
                .hasName(variableName)
                .hasTaskId(taskId)
                .hasType(variableType);
    }

    @Test
    public void getHandledEventClass() throws Exception {
        //when
        Class<? extends ProcessEngineEvent> handledEventClass = handler.getHandledEventClass();

        //then
        assertThat(handledEventClass).isEqualTo(VariableCreatedEvent.class);
    }
}