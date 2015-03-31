package com.android.andreas.runinterval;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SessionManager extends BroadcastReceiver {

    public static String BROADCAST_ACTION = "com.android.andreas.NORMAL_UPDATE";

    private static SessionManager mInstance;

    private boolean isSessionActive;
    private boolean waitingForStart;
    private boolean exerciseActive;

    private int totalDistance;
    private IntervalType intervalType;
    private int intervalValue;
    private int nrPushUps;
    private int nrSitUps;

    private int metersLeft;
    private long timeLeft;

    private ExerciseType nextExercise;

    // TIMER STUFF
    private Handler timerHandler;

    private long startTime;
    private long totalTime;
    private long runningTime;

    private Context ctx;

    public static SessionManager getInstance() {
        return mInstance;
    }

    public static SessionManager getInstance(Context _appContext) {
        if (mInstance == null) {
            mInstance = new SessionManager(_appContext);
        }
        return mInstance;
    }

    public SessionManager(Context _context) {
        ctx = _context;
        isSessionActive = false;
        metersLeft = 0;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("context", "received");
        ctx = context;
    }

    public boolean setUpNewSession(int _totalDistance, IntervalType _intervalType, int _intervalValue, int _nrPushUps, int _nrSitUps) {
        if (!isSessionActive) {
            totalDistance = _totalDistance;
            intervalType = _intervalType;
            intervalValue = _intervalValue;
            nrPushUps = _nrPushUps;
            nrSitUps = _nrSitUps;

            if (nrPushUps > 0) {
                nextExercise = ExerciseType.PUSH_UPS;
            } else if (nrSitUps > 0) {
                nextExercise = ExerciseType.SIT_UPS;
            }

            if (intervalType == IntervalType.DISTANCE) {
                metersLeft = intervalValue;
            }

            totalTime = 0L;
            runningTime = 0L;
            waitingForStart = true;
            exerciseActive = false;
        }
        return waitingForStart;
    }

    public void startSession() {
        if (waitingForStart) {
            startTime = SystemClock.uptimeMillis();
            timerHandler = new Handler();
            timerHandler.postDelayed(updateTimerThread, 0);
        }
    }

    public void ranDistance(int _leftDistance) {
        if (isSessionActive) {
            totalDistance -= _leftDistance;
            if (totalDistance <= 0) {
                // TODO - finished run
            }

            if (intervalType == IntervalType.DISTANCE) {
                metersLeft -= _leftDistance;
                if (metersLeft <= 0) {
                    // TODO - do next exercise
                }
            }
        }
    }

    public void finishedExercise() {
        setNextExerciseType();

        if (intervalType == IntervalType.DISTANCE) {
            metersLeft = intervalValue;
        }
    }

    private void setNextExerciseType() {
        if (nextExercise == ExerciseType.PUSH_UPS) {
            if (nrSitUps > 0) {
                nextExercise = ExerciseType.SIT_UPS;
            }
        } else if (nextExercise == ExerciseType.SIT_UPS) {
            if (nrPushUps > 0) {
                nextExercise = ExerciseType.PUSH_UPS;
            }
        }
    }


    // TIMER STUFF

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            totalTime = SystemClock.uptimeMillis() - startTime;

            int secs = (int) (totalTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            Log.i("Time to run:", mins + ":" + String.format("%02d", secs));
            timerHandler.postDelayed(this, 1500);

            sendNormalSessionUpdates();
        }
    };


    // BROADCAST STUFF

    public void sendNormalSessionUpdates() {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.setAction(BROADCAST_ACTION);
        // add data
        intent.putExtra("message", "data");

//        ctx.sendBroadcast(intent);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }
}
