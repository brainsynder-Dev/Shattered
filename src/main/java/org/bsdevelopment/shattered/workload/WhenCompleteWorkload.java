package org.bsdevelopment.shattered.workload;

public class WhenCompleteWorkload implements Workload {

    private final Runnable runnable;

    public WhenCompleteWorkload(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public boolean compute() {
        runnable.run();
        return false;
    }

}