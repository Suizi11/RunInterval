package com.android.andreas.runinterval;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SessionManager extends BroadcastReceiver {

    private static final String TAG = "SessionManager";

    public static final String BROADCAST_ACTION_NORMAL = "com.android.andreas.NORMAL_UPDATE";
    public static final String BROADCAST_ACTION_EXERCISE = "com.android.andreas.EXERCISE_UPDATE";
    public static final String BROADCAST_ACTION_FINISH = "com.android.andreas.FINISH_UPDATE";

    public static final String TOTAL_DISTANCE_KEY = "distance";
    public static final String TOTAL_TIME_KEY = "time";
    public static final String TOTAL_RUN_TIME_KEY = "run-time";
    public static final String INTERVAL_VALUE_KEY = "interval-value";

    public static final String EXERCISE_TYPE_KEY = "exercise-type";
    public static final String EXERCISE_VALUE_KEY = "exercise-value";


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
            waitingForStart = true;
            exerciseActive = false;

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

            setBackIntervalValues();

            totalTime = 0L;
            runningTime = 0L;
        }
        return waitingForStart;
    }

    public void startSession() {
        if (waitingForStart) {
            waitingForStart = false;
            isSessionActive = true;

            startTime = SystemClock.uptimeMillis();
            timerHandler = new Handler();
            timerHandler.postDelayed(updateTimerThread, 0);
        }
    }

    public void endSession() {
        isSessionActive = false;
        sendFinishUpdate();
    }

    public IntervalType getIntervalType() {
        return intervalType;
    }

    public void ranDistance(int _leftDistance) {
        if (isSessionActive) {
            totalDistance -= _leftDistance;
            if (totalDistance <= 0) {
                endSession();
                return;
            }

            if (intervalType == IntervalType.DISTANCE) {
                metersLeft -= _leftDistance;
                if (metersLeft <= 0) {
                    exerciseActive = true;
                    sendExerciseUpdates();
                }
            }
        }
    }

    public void setBackIntervalValues() {
        if (intervalType == IntervalType.DISTANCE) {
            metersLeft = intervalValue;
        } else if (intervalType == IntervalType.TIME) {
            timeLeft = intervalValue * 60 * 1000;
        }
    }

    public void finishedExercise() {
        setNextExerciseType();
        exerciseActive = false;

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

            long totalTimeBefore = totalTime;
            totalTime = SystemClock.uptimeMillis() - startTime;
            long difference = totalTime - totalTimeBefore;
            if (!exerciseActive) {
                runningTime += difference;
                if (intervalType == IntervalType.TIME) {
                    timeLeft -= difference;
                    if (timeLeft < 0) {
                        sendExerciseUpdates();
                    }
                }
            }

            int secs = (int) (totalTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            Log.i("Time to run:", mins + ":" + String.format("%02d", secs));
            timerHandler.postDelayed(this, 500);

            sendNormalUpdates();
        }
    };


    // BROADCAST STUFF

    public void sendNormalUpdates() {
        Intent intent = new Intent(BROADCAST_ACTION_NORMAL);
        intent.putExtra(TOTAL_DISTANCE_KEY, totalDistance);
        intent.putExtra(TOTAL_TIME_KEY, totalTime);
        if (intervalType == IntervalType.DISTANCE) {
            intent.putExtra(INTERVAL_VALUE_KEY, metersLeft);
        } else if (intervalType == IntervalType.TIME) {
            intent.putExtra(INTERVAL_VALUE_KEY, timeLeft);
        }
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }

    public void sendExerciseUpdates() {
        if (nextExercise != null) {
            Intent intent = new Intent(BROADCAST_ACTION_EXERCISE);
            intent.putExtra(EXERCISE_TYPE_KEY, nextExercise);
            if (nextExercise == ExerciseType.PUSH_UPS) {
                intent.putExtra(EXERCISE_VALUE_KEY, nrPushUps);
            } else if (nextExercise == ExerciseType.SIT_UPS) {
                intent.putExtra(EXERCISE_VALUE_KEY, nrSitUps);
            }
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
        } else {
            Log.i(TAG, "no exercise is given");
        }
    }

    public void sendFinishUpdate() {
        Intent intent = new Intent(BROADCAST_ACTION_FINISH);
        intent.putExtra(TOTAL_DISTANCE_KEY, totalDistance);
        intent.putExtra(TOTAL_TIME_KEY, totalTime);
        intent.putExtra(TOTAL_RUN_TIME_KEY, runningTime);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }
}
