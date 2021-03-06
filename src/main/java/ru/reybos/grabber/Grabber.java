package ru.reybos.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.reybos.grabber.parse.Parse;
import ru.reybos.grabber.parse.SqlRuParse;
import ru.reybos.grabber.store.Post;
import ru.reybos.grabber.store.PsqlStore;
import ru.reybos.grabber.store.Store;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());

    public Store store() throws SQLException {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {
        try (InputStream in =
                     Grabber.class.getClassLoader().getResourceAsStream("grabber.properties")
        ) {
            cfg.load(in);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            List<Post> posts;
            try {
                posts = parse.list("https://www.sql.ru/forum/job-offers");
            } catch (IOException e) {
                throw new JobExecutionException("Ошибка загрузки данных с сайта", e);
            }
            for (Post post : posts) {
                try {
                    store.save(post);
                } catch (SQLException e) {
                    LOG.error("Ошибка сохранения вакансии в БД");
                }
            }
        }
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        out.write("<!DOCTYPE html>".getBytes());
                        out.write("<html>".getBytes());
                        out.write("<head>".getBytes());
                        out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">".getBytes());
                        out.write("<title>Grabber</title>".getBytes());
                        out.write("</head>".getBytes());
                        out.write("<body>".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(post.toString().getBytes());
                            out.write(System.lineSeparator().getBytes());
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
        grab.web(store);
    }
}