package com.tigerit.soa.service;

import com.tigerit.soa.request.task.TaskRequest;
import com.tigerit.soa.request.task.TaskSearchRequest;
import com.tigerit.soa.response.ServiceResponse;
import com.tigerit.soa.response.ServiceResponseExtended;

import javax.validation.Valid;

public interface TaskService {

    ServiceResponse createTask(TaskRequest request, String username);

    ServiceResponse updateTask(TaskRequest request, String username);

    ServiceResponse deleteTask(String taskId, String projectId, String username);

    ServiceResponse detailTask(String taskId, String projectId, String username);

    ServiceResponseExtended actionList(TaskSearchRequest request, String username);

    ServiceResponseExtended searchTask(@Valid TaskSearchRequest request, String username);

    void syncProjectHeaderUpdate(String projectId, String projectName);
}
