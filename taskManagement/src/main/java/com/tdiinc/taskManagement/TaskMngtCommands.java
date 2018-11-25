package com.tdiinc.taskManagement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import com.tdiinc.taskManagement.model.Comment;
import com.tdiinc.taskManagement.model.Task;
import com.tdiinc.taskManagement.model.TaskDB;
import com.tdiinc.taskManagement.model.TaskStatus;

import com.tdiinc.taskManagement.services.TaskService;
import com.tdiinc.taskManagement.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.annotation.Resource;

/**
 * @author hbensaid
 * @email hedi.bensaid@gmail.com
 */
@ShellComponent
public class TaskMngtCommands {

    private static final Logger log = LoggerFactory.getLogger(TaskMngtCommands.class);

    @Autowired
    private TaskService taskService;

    @Resource(name = "dateUtil")
    private DateUtil dateUtil;

    @Resource(name = "taskDB")
    private TaskDB taskDB;

    private final PrintStream out = System.out;

    // TODO display urgent tasks with another colour (this can be done only on unix
    // like systems
    // TODO add the possibility to specify tags
    // TODO correct the display of special characters in the output
    // TODO add the Age column
    /**
     * Displays all the pending tasks sorted by date
     */
    @ShellMethod("Display Tasks")
    public void list() {
        List<Task> tasks = new ArrayList<>(taskService.getPendingTasks().values());
        tasks.sort((task1, task2) -> task1.getDueDate().compareTo(task2.getDueDate()));
        printTasks(tasks);
    }

    /**
     * Search all the pending tasks having in there description the given text
     * to search
     *
     * @param textToSearch: date
     */
    @ShellMethod("Search in Tasks")
    public void grep(String textToSearch) {

        Collection<Task> tasks = taskService.getPendingTasks().values().stream()
                .filter(task -> StringUtils.containsIgnoreCase(task.getDescription(), textToSearch))
                .collect(Collectors.toList());
        printTasks(tasks);
    }

    /**
     * Search all tasks having in there description the given text to search
     *
     * @param textToSearch
     */
    @ShellMethod("Search in Tasks")
    public void grepAll(String textToSearch) {

        Collection<Task> allTasks = new ArrayList<>();
        allTasks.addAll(taskService.getPendingTasks().values());
        allTasks.addAll(taskDB.getCompletedTasks());
        allTasks.addAll(taskDB.getCanceledTasks());

        Collection<Task> tasks = allTasks.stream()
                .filter(task -> StringUtils.containsIgnoreCase(task.getDescription(), textToSearch))
                .collect(Collectors.toList());

        printTasks(tasks);
    }

    /**
     * Displays all the pending tasks until the given date If no date is given
     * current date is considered
     *
     * @param dueDateStr: due date the due date can be written in format
     * [dd/MM/yyyy][dd-MM-yyyy] or can be written as follows TOMORROW, TODAY,
     * MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
     */
    @ShellMethod("Display Tasks for a given date")
    public void listDay(@ShellOption(defaultValue = "TODAY") String dueDateStr) {
        Date dueDate = new Date();
        if (dueDateStr != null && !"".equals(dueDateStr)) {
            dueDate = dateUtil.computeDueDate(dueDateStr);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        int day = cal.get(Calendar.DAY_OF_MONTH) + 1;
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        Date searchDate = dateUtil.parseDate(day + "/" + month + "/" + year);

        List<Task> tasks = taskService.getPendingTasks().values().stream()
                .filter(task -> task.getDueDate().compareTo(searchDate) <= 0).collect(Collectors.toList());
        tasks.sort((task1, task2) -> task1.getDueDate().compareTo(task2.getDueDate()));

        printTasks(tasks);

    }

    /**
     * Add a new Task, if no due date is given current date is considered
     *
     * @param description: task description
     * @param dueDateStr: due date, 'TODAY' by default
     * @param project: project name: 'MISC' by default
     */
    @ShellMethod("Add Task")
    public void add(String description, @ShellOption(defaultValue = "TODAY") String dueDateStr,
            @ShellOption(defaultValue = "MISC") String project) {

        Date dueDate = new Date();
        if (dueDateStr != null && !"".equals(dueDateStr)) {
            dueDate = dateUtil.computeDueDate(dueDateStr);
        }

        Task task = new Task(description, dueDate);
        taskService.addPendingTask(task);
        out.println(task);
    }

    /**
     * set the status of the given task to Complete
     *
     * @param taskId
     */
    @ShellMethod("Complete Task")
    public void complete(Long taskId) {
        Task task = taskService.getPendingTasks().get(taskId);
        if (task != null) {
            taskService.completeTask(task);
            out.println(task);
            out.println("completed");
        } else {
            out.println("No task with id:'" + taskId + "' found!!");
        }
    }

    /**
     * set the status of the given task to Canceled
     *
     * @param taskId
     */
    @ShellMethod("Cancel Task")
    public void cancel(Long taskId) {
        try {
            if (confirm()) {
                Task task = taskService.getPendingTasks().get(taskId);
                if (task != null) {
                    task.setStatus(TaskStatus.Canceled);
                    task.setCompleteDate(new Date());
                    taskService.cancelTask(task);
                    out.println(task);
                    out.println("canceled");
                } else {
                    out.println("No task with id:'" + taskId + "' found!!");
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Postpone the due date of the given date to another date
     *
     * @param taskId
     * @param dueDateStr
     */
    @ShellMethod("Change DueDate")
    public void postpone(Long taskId, String dueDateStr) {
        try {
            if (confirm()) {
                Task task = taskService.getPendingTasks().get(taskId);
                if (task != null) {
                    Date newDate = new Date();
                    if (dueDateStr != null && !"".equals(dueDateStr)) {
                        newDate = dateUtil.computeDueDate(dueDateStr);
                    }
                    task.setDueDate(newDate);
                    taskService.synchronizeToFile();
                    out.println(task);
                    out.println("dueDate:" + task.getDueDate());
                } else {
                    out.println("No task with id:'" + taskId + "' found!!");
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Add a comment to the given task One task may have more than one comment
     *
     * @param taskId
     * @param textComment
     */
    @ShellMethod("Add Comment to Task")
    public void comment(Long taskId, String textComment) {

        Task task = taskService.getPendingTasks().get(taskId);
        if (task != null) {
            taskService.addComment(textComment, task);
            taskService.synchronizeToFile();
            out.println(task);
            out.println("completed");
        } else {
            out.println("No task with id:'" + taskId + "' found!!");
        }
    }

    /**
     * Displays information about a given task
     *
     * @param taskId
     */
    @ShellMethod("Info of task")
    public void info(Long taskId) {
        Task task = taskService.getPendingTasks().get(taskId);
        if (task != null) {
            out.println("task id:" + task.getTaskId());
            out.println("description:" + task.getDescription());
            out.println("due date:" + task.getDueDate());
            out.println("status:" + task.getStatus());
            if (task.getProject() != null) {
                out.println("Project:" + task.getProject());
            }
            if (task.getPriority() != null) {
                out.println("Priority:" + task.getPriority());
            }
            if (task.getComments() != null) {
                Collection<Comment> comments = task.getComments();
                for (Comment comment : comments) {
                    out.println("---------------");
                    out.println("Date:" + comment.getCreationDate());
                    out.println("Comment:" + comment.getText());
                }
            }
        } else {
            out.println("No task with id:'" + taskId + "' found!!");
        }
    }

    /**
     * Print given tasks to the console
     *
     * @param tasks
     */
    private void printTasks(Collection<Task> tasks) {
        int iTask = 0;

        traceTextWithColour(String.format("%-20s %-100s%-20s %-20s", "ID", "Description", "Due Date", "status"),
                AnsiColor.BRIGHT_CYAN);
        traceTextWithColour(String.format("%-20s %-100s%-20s %-20s", "---------------", "---------------",
                "---------------", "---------------"), AnsiColor.BRIGHT_CYAN);
        for (Task oneTask : tasks) {
            iTask++;
            String textToLog;
            AnsiColor textColour = AnsiColor.DEFAULT;
            String taskDescription;
            if (oneTask.getComments() != null && oneTask.getComments().size() > 0) {
                taskDescription = "(" + oneTask.getComments().size() + ")" + oneTask.getDescription();

            } else {
                taskDescription = oneTask.getDescription();

            }
            if (taskDescription.length() > 100) {
                taskDescription = taskDescription.substring(0, 95) + "...";
            }
            textToLog = String.format("%-20s %-100s%-20s %-20s", oneTask.getTaskId(), taskDescription,
                    oneTask.getDueDate(), oneTask.getStatus().name());

            if (oneTask.getDueDate().compareTo(new Date()) <= 0) {
                textColour = AnsiColor.RED;
            }

            traceTextWithColour(textToLog, textColour);
        }
        out.println("");
        out.println(iTask + " tasks.");
    }

    /**
     * Ask for confirmation before the execution of the action
     *
     * @return
     * @throws IOException
     */
    private boolean confirm() throws IOException {
        out.println("Are you sure you want to proceed?");
        String line = new LineNumberReader(new InputStreamReader(System.in)).readLine();
        return "y".equalsIgnoreCase(line);
    }

    /**
     * trace text with given color
     *
     * @param text
     * @param color
     */
    private void traceTextWithColour(String text, AnsiColor color) {
        out.println(AnsiOutput.toString(color, text, AnsiColor.DEFAULT));
    }

}
