package com.tdiinc.taskManagement.init;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class TaskManagementInit implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskManagementInit.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOG.info("Application Initialised");
    }

}
