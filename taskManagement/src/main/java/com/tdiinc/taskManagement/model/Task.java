package com.tdiinc.taskManagement.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task {

    private Long taskId;

    private String description;
    private Date dueDate;
    private Date completeDate;
    private Date creationDate;

    private List<Comment> comments;

    private String project;
    private TaskPriority priority;

    private TaskStatus status;

    public Task() {
        comments = new ArrayList<>();
    }

    public Task(String description, Date dueDate) {
        super();
        this.description = description;
        this.dueDate = dueDate;
        this.status = TaskStatus.Pending;
        this.creationDate = new Date();
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Long getTaskId() {
        return taskId;
    }

    /**
     * @return the status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * @return the completeDate
     */
    public Date getCompleteDate() {
        return completeDate;
    }

    /**
     * @param completeDate the completeDate to set
     */
    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "taskId=" + taskId + ", description=" + description + ", dueDate=" + dueDate;
    }

}
