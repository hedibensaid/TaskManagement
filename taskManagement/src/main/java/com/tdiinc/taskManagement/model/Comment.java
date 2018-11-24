package com.tdiinc.taskManagement.model;

import java.util.Date;

/**
 *
 * @author hbensaid
 */
public class Comment {

    private Long id;

    private String text;

    private Date creationDate;

    private Long fk_task;

    public Long getFk_task() {
        return fk_task;
    }

    public void setFk_task(Long fk_task) {
        this.fk_task = fk_task;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
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

    @Override
    public String toString() {
        return "Comment{" + "id=" + id + ", text=" + text + ", creationDate=" + creationDate + ", fk_task=" + fk_task + '}';
    }

}
