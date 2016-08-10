package edu.uw.jonanv.motiongame;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Vincent Jonany
 * This class controls the state of the application, gestures, touches and other UI interactions
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private DrawingSurfaceView view;
    private GestureDetector detector;
    private Ball currentBall;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SoundPool soundPool;

    Integer[] soundId = new Integer[2];
    Boolean[] loaded = {false, false};

    /**
     * Creates the UI for the application
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (DrawingSurfaceView)findViewById(R.id.drawingView);
        detector = new GestureDetector(this, new MyGestureListener());
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);


        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if(sensor == null) {
            Toast.makeText(MainActivity.this, "No sensors available", Toast.LENGTH_LONG).show();
            finish();
        }

        initializeSoundPool();
    }

    /**
     * initialize sound pool, and load the sounds
     */
    public void initializeSoundPool() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(aa)
                    .setMaxStreams(5)
                    .build();

        } else {
            soundPool = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0);
        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if(status == 0) {
                    if(sampleId == soundId[0]) {
                        loaded[0] = true;
                    }
                    if(sampleId == soundId[1]) {
                        loaded[1] = true;
                    }
                }
            }
        });
        soundId[0] = soundPool.load(this, R.raw.computererror, 1);
        soundId[1] = soundPool.load(this, R.raw.doorbell, 1);
    }

    /**
     * onresume of the application
     */
    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
        initializeSoundPool();
    }

    /**
     * on pause
     */
    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, sensor);
        super.onPause();
        soundPool.release();
    }

    /**
     * event listener when there is a touch event.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean gestured = detector.onTouchEvent(event);
        sensorManager.unregisterListener(this, sensor);

        float x = event.getX();
        float y = event.getY();

        if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            currentBall = new Ball(x, y);
            view.ballSet.add(currentBall);
            if(loaded[1]) {
                soundPool.play(soundId[1], 1, 1, 1, 0, 1);
            }
            return true;
        } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE) {
            currentBall.cx = x;
            currentBall.cy = y;
        } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_POINTER_DOWN) {

        }
        return super.onTouchEvent(event);
    }

    /**
     * Do actions when accelemarator sensor detect changes
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        for(Ball balls : view.ballSet) {
            balls.dx = values[0] * balls.radius * 1.5f;
            balls.dy = values[1] * balls.radius * 1.5f;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * listener class to listen to gestures
     */
    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * on down of the touch
         * @param e
         * @return
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return true; //we got this
        }

        /**
         * when detects fling
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(loaded[0]) {
                soundPool.play(soundId[0], 1, 1, 1, 0 ,1);
            }
            if(currentBall != null) {
                currentBall.dx = .005f*velocityX;
                currentBall.dy = .005f*velocityY;
            }
            return true; //we handled this
        }
    }
}
