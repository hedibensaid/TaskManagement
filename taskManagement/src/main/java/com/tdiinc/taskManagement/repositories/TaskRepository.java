package com.tdiinc.taskManagement.repositories;

import com.tdiinc.taskManagement.model.Task;
import com.tdiinc.taskManagement.model.TaskStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {

    List<Task> findByProject(String project);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :dueDate and t.status=0 ORDER BY t.dueDate")
    List<Task> findBeforeDueDate(@Param("dueDate") Date dueDate);

    @Query("SELECT t FROM Task t WHERE upper(t.description) like %:searchStr% and t.status=0")
    List<Task> searchInTasks(@Param("searchStr") String searchStr); 

    @Query("SELECT t FROM Task t WHERE upper(t.description) like %:searchStr%")
    List<Task> searchInAllTasks(@Param("searchStr") String searchStr);

    List<Task> findByStatusOrderByDueDateAsc(TaskStatus status);
    
    Task findByTaskId(Long taskId);

}
