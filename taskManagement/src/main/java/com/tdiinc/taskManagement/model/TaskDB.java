package com.tdiinc.taskManagement.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDB {

    private List<Task> completedTasks;

    private Map<Long, Task> pendingTasks;
    private List<Task> canceledTasks;
    private int sequenceId;

    public TaskDB() {
        completedTasks = new ArrayList<>();
        pendingTasks = new HashMap<>();
        canceledTasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        pendingTasks.put(task.getTaskId(), task);
    }

    public List<Task> getCompletedTasks() {
        return completedTasks;
    }

    public List<Task> getCanceledTasks() {
        return canceledTasks;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Map<Long, Task> getPendingTasks() {
        return pendingTasks;
    }

}
