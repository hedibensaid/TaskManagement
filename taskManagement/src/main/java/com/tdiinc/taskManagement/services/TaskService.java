package com.tdiinc.taskManagement.services;

import com.tdiinc.taskManagement.model.Task;
import java.util.Map;


public interface TaskService {

	/**
	 * Add newly created task
	 * @param pendingTask
	 */
    public void addPendingTask(Task pendingTask);

    /**
     * sets the task status to complete
     * @param completeTask
     */
    public void completeTask(Task completeTask);

    /**
     * sets the task status to cancel
     * @param canceledTask
     */
    public void cancelTask(Task canceledTask);

    /**
     * retrieves the pending tasks
     * @return
     */
    public Map<Long, Task> getPendingTasks();

    /**
     * flushes the in memory data to the hard disk
     */
    public void synchronizeToFile();

}
