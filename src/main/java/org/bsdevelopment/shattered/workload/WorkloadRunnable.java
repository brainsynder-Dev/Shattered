package org.bsdevelopment.shattered.workload;

import java.util.ArrayDeque;
import java.util.Deque;

public class WorkloadRunnable implements Runnable {

    private static double MAX_MILLIS_PER_TICK = 50.0;
    private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    public WorkloadRunnable (double millis_per_tick) {
        MAX_MILLIS_PER_TICK = millis_per_tick;
    }

    private final Deque<Workload> workloadDeque = new ArrayDeque<>();

    public void addWorkload(Workload workload) {
        this.workloadDeque.add(workload);
    }

    public void whenComplete(Runnable runnable) {
        WhenCompleteWorkload workload = new WhenCompleteWorkload(runnable);
        this.workloadDeque.add(workload);
    }

    @Override
    public void run() {
        long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

        Workload nextLoad;

        while (System.nanoTime() <= stopTime && (nextLoad = this.workloadDeque.poll()) != null) {
            nextLoad.compute();
        }
    }

}