package com.android.andreas.runinterval;


public class SessionManager {

    private static SessionManager mInstance;

    private boolean isSessionActive;

    private int totalDistance;
    private IntervalType intervalType;
    private int intervalValue;
    private int nrPushUps;
    private int nrSitUps;

    private int metersLeft;

    private ExerciseType nextExercise;

    public static SessionManager getInstance() {
        if (mInstance== null) {
            mInstance= new SessionManager();
        }
        return mInstance;
    }

    private SessionManager() {
        isSessionActive = false;
        metersLeft = 0;
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
        }
        return !isSessionActive;
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
}
