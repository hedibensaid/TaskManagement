package com.tdiinc.taskManagement.services;

import com.tdiinc.taskManagement.model.Task;
import java.util.Map;


public interface TaskService {

    public void addPendingTask(Task pendingTask);

    public void completeTask(Task completeTask);

    public void cancelTask(Task canceledTask);

    public Map<Long, Task> getPendingTasks();

    public void synchronizeToFile();

}
