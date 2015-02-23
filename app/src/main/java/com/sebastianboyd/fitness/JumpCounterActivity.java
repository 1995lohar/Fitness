package com.sebastianboyd.fitness;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;


public final class JumpCounterActivity extends CounterActivity {

    private static final int EXERCISE_ID = 9; // Aerobics
    private static final int SHAKE_THRESHOLD = 600;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    JumpCounterService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, JumpCounterService.class);
        startService(intent);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Context context = getApplicationContext();
        if (SaveData.getIntPref(context, String.valueOf(EXERCISE_ID) + "intro")
            == 0) {
            intro();
        }


    }
    /*
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, JumpCounterService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to JumpCounterService, cast the IBinder and get
            // JumpCounterService instance
            JumpCounterService.LocalBinder binder = (JumpCounterService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    */
    @Override
    public void updatePauseState() {
        updatePauseState(R.drawable.bg_button_round_jumps,
                         R.drawable.shape_oval_jumps);
    }

    @Override
    public void sendData(View view) {
        //Log.v("YOLO", String.valueOf(mService.exerciseCount));
        Intent intent = buildDataSenderIntent(EXERCISE_ID);
        startActivity(intent);
    }

    public void intro(){
        //TODO make abstract and not hack
        Context context = getApplicationContext();
        SaveData.setIntPref(context,
                            String.valueOf(EXERCISE_ID) + "intro",
                            1);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Counting Jumps")
                .setMessage("To start counting jumps just put your phone in " +
                            "your " +
                            "pocket and start jumping.")
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .create();

        AlertDialog saveDataDialog = new AlertDialog.Builder(this)
                .setTitle("Saving your data")
                .setMessage("When you are done doing jumps press the big " +
                            "circle to continue.")
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .create();
        saveDataDialog.show();
        dialog.show();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 150) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) /
                              diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    exerciseCount = exerciseCount + 0.5;
                    if (exerciseCount == 1) {
                        startTime = java.lang.System.currentTimeMillis();
                    }
                    endTime = java.lang.System.currentTimeMillis();
                    updateExerciseCount();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
