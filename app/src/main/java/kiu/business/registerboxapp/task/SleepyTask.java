package kiu.business.registerboxapp.task;

import android.os.AsyncTask;

public class SleepyTask extends AsyncTask<Void, Void, Void> {
    private final long milliseconds;

    public SleepyTask(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
