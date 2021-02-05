package ru.reybos.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        try (InputStream rabbitProp =
                     AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")
        ) {
            config.load(rabbitProp);
            int interval = Integer.parseInt(config.getProperty("rabbit.interval"));
            // Конфигурирование.
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            // Создание задачи. Rabbit - требуемые действия
            JobDetail job = newJob(Rabbit.class).build();
            // Создание расписания.
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            // Задача выполняется через триггер.
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            // Загрузка задачи и триггера в планировщик
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}