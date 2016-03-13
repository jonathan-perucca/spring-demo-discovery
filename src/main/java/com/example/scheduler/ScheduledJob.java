package com.example.scheduler;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Component
@EnableScheduling
public class ScheduledJob {

    private static final Logger LOGGER = getLogger(ScheduledJob.class);

    @Scheduled(cron = "*/5 * * * * *")
    public void cronDemo() {
        LOGGER.info("Introspec {} to discover how to make schedule processing", this.getClass());
    }
}
