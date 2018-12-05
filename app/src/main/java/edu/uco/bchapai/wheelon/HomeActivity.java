package edu.uco.bchapai.wheelon;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends Activity implements SensorEventListener{


    private TextView xAView, yAView, zAView, xGView, yGView, zGView;

    String fileName;
    private long timer;
    
    boolean isStarted = false;

    private Sensor accelerometer;
    private Sensor gyroscope;
    private SensorManager accelManager, gyroManager;
    Button btnStart, btnStop, btnAnalyze;
    private Chronometer chronometer;

    // this adds the countdown timer for the start button
    private CountDownTimer countDownTimer;

    TextToSpeech toSpeech;
    int result;
    String text;

    Date date;

    ArrayList<Accelerometer> accelData;
    ArrayList<Gyroscope> gyroData;

    private boolean hasStarted = false;
    private boolean hasStopped = false;

    float accel[] = new float[3];
    float gyro[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        accelData = new ArrayList<Accelerometer>();
        gyroData = new ArrayList<Gyroscope>();

        btnStart = (Button) findViewById(R.id.startbtn);
        btnAnalyze = (Button) findViewById(R.id.btnAnalyze);
        btnStop = (Button) findViewById(R.id.stopbtn);

        accelData = new ArrayList<Accelerometer>();
        gyroData = new ArrayList<Gyroscope>();

        accelManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = accelManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        // Assigning textviews
        xAView = (TextView) findViewById(R.id.xA);
        yAView = (TextView) findViewById(R.id.yA);
        zAView = (TextView) findViewById(R.id.zA);

        gyroManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);


        xGView = (TextView) findViewById(R.id.xG);
        yGView = (TextView) findViewById(R.id.yG);
        zGView = (TextView) findViewById(R.id.zG);


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarted = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarted = false;
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                accelData.clear();
                gyroData.clear();

                saveData(toString());
            }
        });

        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        date = new Date(); // for timer

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accel = event.values;

            xAView.setText("X: " + accel[0]);
            yAView.setText("Y: " + accel[1]);
            zAView.setText("Z: " + accel[2]);

            if(isStarted == true && event.sensor.equals(accelerometer)){
                if(timer != date.getTime()){
                    timer = date.getTime();
                    accelData.add(new Accelerometer(accel,timer));
                }
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){

            gyro = event.values;

            // display gyroscope value to the GUI

            xGView.setText("Pitch (X): " + gyro[0]);
            yGView.setText("Roll (Y): " + gyro[1]);
            zGView.setText("Yaw (Z): " + gyro[2]);

            // if recording is enabled, add the values to the arraylist

            if(isStarted == true && event.sensor.equals(gyroscope)){
                if(timer != date.getTime()){
                    timer = date.getTime();
                    gyroData.add(new Gyroscope(gyro,timer));
                }
            }

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < accelData.size();i++){
            if(i < gyroData.size()){
                sb.append(accelData.get(i).getAccelerometer(0) + ",");
                sb.append(accelData.get(i).getAccelerometer(1) + ",");
                sb.append(accelData.get(i).getAccelerometer(2) + ",");
                sb.append(accelData.get(i).getTime() + ",");

                sb.append(gyroData.get(i).getGyroscope(0) + ",");
                sb.append(gyroData.get(i).getGyroscope(1) + ",");
                sb.append(gyroData.get(i).getGyroscope(2) + ",");
                sb.append(gyroData.get(i).getTime() + "/n");

            }
        }
        return sb.toString();

        //*****************************************

    }

    private void saveData(String data){
        try {
            File file = Environment.getExternalStorageDirectory();

            if(file.canWrite()){
                Date date = new Date();
                Time time = new Time(date.getTime());

                String dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(time);

                fileName = "WheelOnData" + dateFormat + ".csv";

                File newFile = new File(file,fileName);
                FileWriter fileWriter = new FileWriter(newFile);

                BufferedWriter bufferedWriter = new BufferedWriter((fileWriter));
                bufferedWriter.write(data);
                bufferedWriter.close();

            }
            else{
                Toast.makeText(getApplicationContext(),"Error writing the data",Toast.LENGTH_SHORT);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error Saving the File",Toast.LENGTH_SHORT);
        }
    }

}
