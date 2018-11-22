package com.tdiinc.taskManagement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.tdiinc.taskManagement.model.Comment;
import com.tdiinc.taskManagement.model.Task;
import com.tdiinc.taskManagement.model.TaskStatus;
import com.tdiinc.taskManagement.repositories.TaskRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * @author hbensaid
 * @email hedi.bensaid@gmail.com
 */
@ShellComponent
public class TaskMngtCommands {

    private static final Logger log = LoggerFactory.getLogger(TaskMngtCommands.class);

    @Autowired
    private TaskRepository taskRepository;

    private final PrintStream out = System.out;

    //TODO display urgent tasks with another colour (this can be done only on unix like systems
    //TODO add the possibility to specify tags
    //TODO correct the display of special characters in the output
    //TODO add the Age column

    @ShellMethod("Display Tasks")
    public void list() {

        List<Task> tasks = taskRepository.findByStatusOrderByDueDateAsc(TaskStatus.Pending);
        printTasks(tasks);
    }

    @ShellMethod("Search in Tasks")
    public void grep(String criteria) {

        List<Task> tasks = taskRepository.searchInTasks(criteria.toUpperCase());

        printTasks(tasks);
    }

    @ShellMethod("Search in Tasks")
    public void grepAll(String criteria) {

        List<Task> tasks = taskRepository.searchInAllTasks(criteria.toUpperCase());

        printTasks(tasks);
    }


    @ShellMethod("Display Tasks for a given date")
    public void listDay(@ShellOption(defaultValue = "TODAY") String dueDateStr) {
        Date dueDate = new Date();
        if (dueDateStr != null && !"".equals(dueDateStr)) {
            dueDate = computeDueDate(dueDateStr);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        int day = cal.get(Calendar.DAY_OF_MONTH) + 1;
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        Date searchDate = parseDate(day + "/" + month + "/" + year);

        List<Task> tasks = taskRepository.findBeforeDueDate(searchDate);

        printTasks(tasks);

    }

    /**
     * trace text with given color
     *
     * @param text
     * @param color
     */
    private void traceTextWithColour(String text, AnsiColor color) {
        out.println(AnsiOutput.toString(
                color, text, AnsiColor.DEFAULT));
    }

    @ShellMethod("Add Task")
    public void add(String description, @ShellOption(defaultValue = "TODAY") String dueDateStr, @ShellOption(defaultValue = "MISC") String project) {

        Date dueDate = new Date();
        if (dueDateStr != null && !"".equals(dueDateStr)) {
            dueDate = computeDueDate(dueDateStr);
        }

        Task task = new Task(description, dueDate);
        taskRepository.save(task);
        out.println(task);
    }

    @ShellMethod("Complete Task")
    public void complete(Long taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task != null) {
            task.setStatus(TaskStatus.Closed);
            task.setCompleteDate(new Date());
            taskRepository.save(task);
            out.println(task);
            out.println("completed");
        } else {
            out.println("No task with id:'" + taskId + "' found!!");
        }
    }

    @ShellMethod("Cancel Task")
    public void cancel(Long taskId) {
        try {
            if (confirm()) {
                Task task = taskRepository.findByTaskId(taskId);
                if (task != null) {
                    task.setStatus(TaskStatus.Canceled);
                    task.setCompleteDate(new Date());
                    taskRepository.save(task);
                    out.println(task);
                    out.println("completed");
                } else {
                    out.println("No task with id:'" + taskId + "' found!!");
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @ShellMethod("Change DueDate")
    public void postpone(Long taskId, String dueDateStr) {
        try {
            if (confirm()) {
                Task task = taskRepository.findByTaskId(taskId);
                if (task != null) {
                    Date newDate = new Date();
                    if (dueDateStr != null && !"".equals(dueDateStr)) {
                        newDate = computeDueDate(dueDateStr);
                    }
                    task.setDueDate(newDate);
                    taskRepository.save(task);
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


    @ShellMethod("Add Comment to Task")
    public void comment(Long taskId, String textComment) {

        Task task = taskRepository.findByTaskId(taskId);
        if (task != null) {
            Comment comment = new Comment();
            comment.setText(textComment);
            comment.setCreationDate(new Date());
            task.getComments().add(comment);
            taskRepository.save(task);
            out.println(task);
            out.println("completed");
        } else {
            out.println("No task with id:'" + taskId + "' found!!");
        }
    }

    @ShellMethod("Info of task")
    public void info(Long taskId) {
        Task task = taskRepository.findByTaskId(taskId);
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

    private void printTasks(List<Task> tasks) {
        int iTask = 0;

        traceTextWithColour(String.format("%-20s %-100s%-20s %-20s", "ID", "Description", "Due Date", "status"), AnsiColor.BRIGHT_CYAN);
        traceTextWithColour(String.format("%-20s %-100s%-20s %-20s", "---------------", "---------------", "---------------", "---------------"), AnsiColor.BRIGHT_CYAN);
        for (Task oneTask : tasks) {
            iTask++;
            String textToLog;
            AnsiColor textColour = AnsiColor.DEFAULT;
            String taskDescription;
            if (oneTask.getComments().size() > 0) {
                taskDescription = "(" + oneTask.getComments().size() + ")" + oneTask.getDescription();

            } else {
                taskDescription = oneTask.getDescription();

            }
            if (taskDescription.length() > 100) {
                taskDescription = taskDescription.substring(0, 95) + "...";
            }
            textToLog = String.format("%-20s %-100s%-20s %-20s", oneTask.getTaskId(), taskDescription, oneTask.getDueDate(), oneTask.getStatus().name());

            if (oneTask.getDueDate().compareTo(new Date()) <= 0) {
                textColour = AnsiColor.RED;
            }

            traceTextWithColour(textToLog, textColour);
        }
        out.println("");
        out.println(iTask + " tasks.");
    }

    private Date computeDueDate(String dueDate) {
        Date computedDay;
        dueDate = dueDate.toUpperCase();
        switch (dueDate) {
            case "TODAY":
                computedDay = new Date();
                break;
            case "MONDAY":
            case "TUESDAY":
            case "WEDNESDAY":
            case "THURSDAY":
            case "FRIDAY":
            case "SATURDAY":
            case "SUNDAY":
                computedDay = computeToDayOfWeek(dueDate);
                break;
            case "TOMORROW":
                computedDay = computeToNextDayOfWeek();
                break;
            case "NEXTWEEK":
                computedDay = computeToDayOfWeek("MONDAY");
                break;
            default:
                computedDay = parseDate(dueDate);
        }

        return computedDay;
    }


    private Date computeToNextDayOfWeek() {

        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        c.add(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }

    private Date computeToDayOfWeek(String dueDate) {

        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        c.add(Calendar.DAY_OF_MONTH, 1);

        while (getDayOfWeek(c.getTime()).toString() == null ? dueDate != null : !getDayOfWeek(c.getTime()).toString().equals(dueDate.toUpperCase())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        return c.getTime();
    }

    private DayOfWeek getDayOfWeek(Date refDate) {
        LocalDate today = refDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return today.getDayOfWeek();
    }

    /**
     * parse the date of pattern [dd/MM/yyyy][dd-MM-yyyy]
     *
     * @param dueDate
     * @return
     */
    private Date parseDate(String dueDate) {
        Date computedDay;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd/MM/yyyy][dd-MM-yyyy]");
        LocalDate realDueDate = LocalDate.parse(dueDate, formatter);
        computedDay = Date.from(realDueDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return computedDay;
    }

    private boolean confirm() throws IOException {
        System.out.println("Are you sure you want to proceed?");
        String line = new LineNumberReader(new InputStreamReader(System.in)).readLine();
        return "y".equalsIgnoreCase(line);
    }

}
