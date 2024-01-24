package ru.itmo;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskStatUtil {

    private AtomicInteger tasksFinished = new AtomicInteger(0);
    private int tasksExpected;
    private int freqNum;

    public TaskStatUtil(int tasksExpected) {
        this.tasksExpected = tasksExpected;
        freqNum = tasksExpected / 10;
    }

    public TaskStatUtil(int tasksExpected, int reportEveryNthPercent) {
        this.tasksExpected = tasksExpected;
        freqNum = tasksExpected / reportEveryNthPercent;
    }

    public void incrementAndInfo() {
        int val = tasksFinished.incrementAndGet();
        if(val % freqNum == 0) {
//            System.out.println("rendering " + (float)val / tasksExpected * 100 + "%");
            System.out.printf("rendering %.1f%%\n", (float)val / tasksExpected * 100);
        }
    }
}
