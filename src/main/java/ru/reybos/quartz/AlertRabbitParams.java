package ru.reybos.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbitParams {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (FileReader rabbitProp = new FileReader(
                "./src/main/resources/rabbit.properties"
        )) {
            properties.load(rabbitProp);
        }
        int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
        Class.forName(properties.getProperty("jdbc.driver"));
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.username"),
                properties.getProperty("jdbc.password")
        )) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail()
                    .getJobDataMap()
                    .get("connection");
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "insert into rabbit (created_date) values (current_timestamp)"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
