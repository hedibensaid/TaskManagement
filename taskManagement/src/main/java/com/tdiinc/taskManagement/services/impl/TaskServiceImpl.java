package com.tdiinc.taskManagement.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tdiinc.taskManagement.model.Comment;
import com.tdiinc.taskManagement.model.Task;
import com.tdiinc.taskManagement.model.TaskDB;
import com.tdiinc.taskManagement.model.TaskStatus;
import com.tdiinc.taskManagement.services.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource(name = "taskDB")
    private TaskDB taskDB;

    @Resource(name = "jsonFilePath")
    private String jsonFilePath;

    @Override
    public void addPendingTask(Task pendingTask) {
        taskDB.setSequenceId(taskDB.getSequenceId() + 1);
        pendingTask.setTaskId((long) taskDB.getSequenceId());
        taskDB.getPendingTasks().put(pendingTask.getTaskId(), pendingTask);
        synchronizeToFile();
    }

    @Override
    public void completeTask(Task completeTask) {
        //remove from pending tasks
        completeTask.setStatus(TaskStatus.Closed);
        completeTask.setCompleteDate(new Date());
        taskDB.getPendingTasks().remove(completeTask.getTaskId());
        //add to complete tasks
        taskDB.getCompletedTasks().add(completeTask);
        synchronizeToFile();
    }

    @Override
    public void cancelTask(Task canceledTask) {
        //remove from pending tasks
        taskDB.getPendingTasks().remove(canceledTask.getTaskId());
        //add to canceled tasks
        taskDB.getCanceledTasks().add(canceledTask);
        synchronizeToFile();
    }

    @Override
    public Map<Long, Task> getPendingTasks() {
        return taskDB.getPendingTasks();
    }

    /**
     * synchronises all the tasks to the file
     */
    @Override
    public void synchronizeToFile() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Path path = Paths.get(jsonFilePath);

        try {
            Files.write(path, gson.toJson(taskDB).getBytes());
        } catch (IOException ex) {
            Logger.getLogger(TaskServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Add comment to the given Task
     *
     * @param commentStr
     * @param task
     */
    @Override
    public void addComment(String commentStr, Task task) {
        List<Comment> comments = task.getComments();
        if (comments == null) {
            comments = new ArrayList<Comment>();
        }
        Comment comment = new Comment();
        comment.setText(commentStr);
        comment.setCreationDate(new Date());
        comments.add(comment);
        task.setComments(comments);
    }
}
