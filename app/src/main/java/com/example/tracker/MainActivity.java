package com.example.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView x,y,z,textView,pos,vel,textView4;
    private double initial_mag,dist = 0;
    private int step_count = 0;

//    private float[] velocity = {0.0f, 0.0f}; // Current velocity (x, y)
//    private float[] acceleration = {0.0f, 0.0f, 0.0f}; // Current acceleration (x, y, z)
//    private long lastUpdateTime = 0; // Last update time of accelerometer readings
//    private float[] position = {0.0f, 0.0f}; // Current position (x, y)
    // Constants for PDR
    private static final float GRAVITY = 9.81f; // Acceleration due to gravity
    private static final float STEP_THRESHOLD = 1; // Threshold for step detection 3.5f
    private static final float STRIDE_LENGTH = 0.7f; // Average stride length in meters
    private static final int ACCELERATION_THRESHOLD = 3;
    private long lastStepTime;
    private static final int STEP_INTERVAL_MAX_MS = 1000;
    private static final int STEP_INTERVAL_MIN_MS = 100;
    private int lastZAxisAcceleration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if(sensorManager!=null){
            Sensor geosensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);

            if(geosensor!=null){
                sensorManager.registerListener( MainActivity.this ,geosensor,sensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        if(sensorManager != null){

            Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor geosensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
            sensorManager.registerListener( MainActivity.this ,geosensor,sensorManager.SENSOR_DELAY_NORMAL);
            if(accel != null){
                sensorManager.registerListener(this,accel,SensorManager.SENSOR_DELAY_NORMAL);
            }

        }else {
            Toast.makeText(this,"no sensor",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        z = findViewById(R.id.z);
        textView = findViewById(R.id.textView);
        pos = findViewById(R.id.textView2);
        vel = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);

        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
//            x.setText(String.valueOf(sensorEvent.values[0]));
//            y.setText(String.valueOf(sensorEvent.values[1]));
//            z.setText(String.valueOf(sensorEvent.values[2]));
//
//            long currentTime = System.currentTimeMillis();
//            float timeInterval = (currentTime - lastUpdateTime) / 1000.0f; // Convert to seconds
//            lastUpdateTime = currentTime;

            float[] accelerationValues = sensorEvent.values.clone();

            accelerationValues[2] -= GRAVITY;

            // Calculate magnitude of acceleration
            double accelerationMagnitude =Math.sqrt(accelerationValues[0]*accelerationValues[0] + accelerationValues[1]*accelerationValues[1] + accelerationValues[2]*accelerationValues[2]);
            double mag_delta = accelerationMagnitude - initial_mag;
            initial_mag = accelerationMagnitude;




//            // Update velocity and position using acceleration and time interval
//            velocity[0] += accelerationValues[0] * timeInterval;
//            velocity[1] += accelerationValues[1] * timeInterval;
//            position[0] += velocity[0] * timeInterval + 0.5f * accelerationValues[0] * timeInterval * timeInterval;
//            position[1] += velocity[1] * timeInterval + 0.5f * accelerationValues[1] * timeInterval * timeInterval;

            // Detect steps using threshold-based method
            if (mag_delta > STEP_THRESHOLD) {
                // Update position based on stride length
//                position[0] += STRIDE_LENGTH * Math.cos(Math.atan2(velocity[1], velocity[0]));
//                position[1] += STRIDE_LENGTH * Math.sin(Math.atan2(velocity[1], velocity[0]));
                step_count++;
                dist += 1/STRIDE_LENGTH;
                DecimalFormat df = new DecimalFormat("#.###");
                String formatted = df.format(dist);
                pos.setText("Steps : "+Math.round(step_count));
                vel.setText("Distance : "+formatted+" m");
            }

//            // Save acceleration readings for next update
//            acceleration[0] = accelerationValues[0];
//            acceleration[1] = accelerationValues[1];
//            acceleration[2] = accelerationValues[2];
//
//            pos.setText(String.valueOf(position[0])+" "+String.valueOf(position[1]));
//            vel.setText(String.valueOf(velocity[0])+" "+String.valueOf(velocity[1]));

            long currentTime = System.currentTimeMillis();
            long timeDiff = currentTime - lastStepTime;



            if (mag_delta < ACCELERATION_THRESHOLD) {
                textView.setText("walking");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // your code here
//                        textView.setText("walking");
//                    }
//                }, 10000); // delay for 1 second (1000 milliseconds)
            }

            if(mag_delta > ACCELERATION_THRESHOLD && timeDiff > STEP_INTERVAL_MIN_MS){
                textView.setVisibility(View.INVISIBLE);
                textView4.setVisibility(View.VISIBLE);
                textView.setText("Stairs !!!!");
//                new Async().execute("Stairs !!!!");
                lastStepTime = currentTime;
//                try {
//                    Thread.sleep(1000); // delay for 1 second (1000 milliseconds)
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // your code here
//                        textView.setText("walking");
                        textView.setVisibility(View.VISIBLE);
                        textView4.setVisibility(View.INVISIBLE);
                    }
                }, 1000); // delay for 1 second (1000 milliseconds)
            }
//            else {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // your code here
//                        textView.setText("walking");
//                    }
//                }, 10000); // delay for 1 second (1000 milliseconds)
//            }
//            if(mag_delta < ACCELERATION_THRESHOLD){
////                textView.setText("walking");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // your code here
//                        textView.setText("walking");
//                    }
//                }, 10000); // delay for 1 second (1000 milliseconds)
//            }
//            else {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // your code here
//                        textView.setText("walking ...");
//                    }
//                }, 1000); // delay for 1 second (1000 milliseconds)
////                textView.setText("walking ...");
//            }
//            else if (mag_delta < ACCELERATION_THRESHOLD) {
//                textView.setText("Offf");
//            }
//
//            if (mag_delta > ACCELERATION_THRESHOLD && timeDiff > STEP_INTERVAL_MIN_MS && timeDiff < STEP_INTERVAL_MAX_MS) {
//                // User is likely taking a step
//                // Add your own code to handle this case
//                textView.setText("Stairs !!!!");
//
//                lastStepTime = currentTime;
//            }
            int currentZAxisAcceleration = (int) sensorEvent.values[2];
            int deltaZAxisAcceleration = lastZAxisAcceleration - currentZAxisAcceleration;

            if (deltaZAxisAcceleration > 10) {
                textView.setVisibility(View.INVISIBLE);
                textView4.setVisibility(View.VISIBLE);
                textView4.setText("lift !!!!");
//                new Async().execute("lift !!!!");
//                try {
//                    Thread.sleep(1000); // delay for 1 second (1000 milliseconds)
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                lastZAxisAcceleration = currentZAxisAcceleration;

            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // your code here
//                        textView.setText("walking");
                        textView.setVisibility(View.VISIBLE);
                        textView4.setVisibility(View.INVISIBLE);
                    }
                }, 1000); // delay for 1 second (1000 milliseconds)
            }

        }
        else if (sensorEvent.sensor.getType()==Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR){
            float[] rotationMatrix = new float[9];
            float[] orientation = new float[3];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
//            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            SensorManager.getOrientation(rotationMatrix, orientation);
            float azimuthInDegrees = (float) Math.toDegrees(orientation[0]);
            float pitchInDegrees = (float) Math.toDegrees(orientation[1]);
            float rollInDegrees = (float) Math.toDegrees(orientation[2]);

            String orientationString = String.format("facing towards : %.2f°", azimuthInDegrees);

//            textview3 = findViewById(R.id.textview7);
//            textview3.setText("Value : "+ sensorEvent.values[0]);

            y.setText(orientationString);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private class Async extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... string) {

//            int a = integer[0]/2;
//
//            if(a>2){
//                return String.valueOf(a);
//            }
//            else{
//                return "naaa";
//            }
            return string[0];

        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // your code here
                    textView.setText(s);
                }
            }, 10000); // delay for 1 second (1000 milliseconds)
        }
    }
}
