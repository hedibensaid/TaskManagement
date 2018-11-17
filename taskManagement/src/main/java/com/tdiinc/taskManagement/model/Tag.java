package com.tdiinc.taskManagement.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long fk_task;

    private String label;

    public Tag() {
    }

    public Long getFk_task() {
        return fk_task;
    }

    public void setFk_task(Long fk_task) {
        this.fk_task = fk_task;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Tag{" + "id=" + id + ", fk_task=" + fk_task + ", label=" + label + '}';
    }
    
    
}
