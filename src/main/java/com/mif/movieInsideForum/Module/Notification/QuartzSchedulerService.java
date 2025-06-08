package com.mif.movieInsideForum.Module.Notification;

import com.mif.movieInsideForum.Collection.Notification.Notification;
import com.mif.movieInsideForum.DTO.message.EventMessage;
import com.mif.movieInsideForum.Job.EmailNotificationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuartzSchedulerService {

    private final Scheduler scheduler;

    public void scheduleEmailJob(EventMessage eventMessage, Notification notification, Date scheduleTime  ) {
        try {
            String jobKey = "emailJob-" + eventMessage.getSubscriberId()+ "-" + eventMessage.getEventId();
            // Check if the job already exists and delete it
            if (scheduler.checkExists(new JobKey(jobKey, "emailJobs"))) {
                log.info("Job with ID {} already exists", eventMessage.getEventId());
                return; // Skip scheduling if the job already exists
            }
            
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("eventMessage", eventMessage);
            jobDataMap.put("notification", notification);

            JobDetail jobDetail = JobBuilder.newJob(EmailNotificationJob.class)
                    .withIdentity(jobKey, "emailJobs")
                    .usingJobData(jobDataMap)
                    .build();

            log.info("Scheduled time: {}", scheduleTime);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobKey, "emailTriggers")
                    .startAt(scheduleTime)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Scheduled email notification for Event ID: {}", eventMessage.getEventId());
        } catch (SchedulerException e) {
            log.error("Failed to schedule email notification", e);
        }
    }

    public void deleteScheduledJob(ObjectId eventId, ObjectId userId) {
        try {
            String jobKey = "emailJob-" + userId + "-" + eventId;
            JobKey key = new JobKey(jobKey, "emailJobs");
            if (scheduler.checkExists(key)) {
                scheduler.deleteJob(key);
                log.info("Deleted scheduled job for Event ID: {}, User ID: {}", eventId, userId);
            } else {
                log.info("No scheduled job found for Event ID: {}, User ID: {}", eventId, userId);
            }
        } catch (SchedulerException e) {
            log.error("Failed to delete scheduled job for Event ID: {}, User ID: {}", eventId, userId, e);
        }
    }


}
