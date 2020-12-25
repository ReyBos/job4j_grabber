package ru.reybos.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.reybos.grabber.parse.Parse;
import ru.reybos.grabber.store.Store;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}