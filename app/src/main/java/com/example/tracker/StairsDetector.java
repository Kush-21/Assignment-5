package com.example.tracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StairsDetector implements SensorEventListener {
    private static final int ACCELERATION_THRESHOLD = 15;
    private static final int STEP_INTERVAL_MAX_MS = 1000;
    private static final int STEP_INTERVAL_MIN_MS = 100;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isDetectingStairs;
    private long lastStepTime;

    public StairsDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start() {
        isDetectingStairs = true;
        lastStepTime = 0;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        isDetectingStairs = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isDetectingStairs && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            long currentTime = System.currentTimeMillis();
            long timeDiff = currentTime - lastStepTime;

            if (acceleration > ACCELERATION_THRESHOLD && timeDiff > STEP_INTERVAL_MIN_MS && timeDiff < STEP_INTERVAL_MAX_MS) {
                // User is likely taking a step
                // Add your own code to handle this case

                lastStepTime = currentTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
