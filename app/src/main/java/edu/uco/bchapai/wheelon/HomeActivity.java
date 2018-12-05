package edu.uco.bchapai.wheelon;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class HomeActivity extends Activity implements SensorEventListener{

    // variables to hold the accelerometer, gyroscope and timestamp data
    private ArrayList<Accelerometer> aRecord;
    private ArrayList<Gyroscope> gRecord;
    float mAccl[] = new float[3];
    float mGyro[] = new float[3];
    private long timestamp;

    private Sensor accelerometer, gyroscope; // actual sensor object on phone
    private int aCount, gCount; // Counters for data rows collected by each sensor      /////////////////////////

    Button btnStart,btnAnalyze,btnStop;

    private String fileName;     //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private TextToSpeech talker; // text to speech
    private SensorManager sensorManager; // for setting the sensor delay

    private boolean buttonRecord = false; // check if the data are being read or not

    private TextView textAcclX, textAcclY, textAcclZ, textGyroX, textGyroY, textGyroZ, textViewStatus;
    private Chronometer chronometerRecord;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnStart = (Button) findViewById(R.id.startbtn);
        btnAnalyze = (Button) findViewById(R.id.analyzebtn);
        btnStop = (Button) findViewById(R.id.endbtn);

        aRecord = new ArrayList<Accelerometer>();
        gRecord = new ArrayList<Gyroscope>();
        aCount = 0;
        gCount = 0;

        // GUI Items

        textAcclX = (TextView) findViewById(R.id.xA);
        textAcclY = (TextView) findViewById(R.id.yA);
        textAcclZ = (TextView) findViewById(R.id.zA);
        textGyroX = (TextView) findViewById(R.id.xG);
        textGyroY = (TextView) findViewById(R.id.yG);
        textGyroZ = (TextView) findViewById(R.id.zG);

        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        chronometerRecord = (Chronometer) findViewById(R.id.chronometerRecord);
        // Get the sensor manager from the system service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // check if accelerometer is present on device
        if(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0){
            textViewStatus.setText("No Accelerometer installed");
        } else { // register listener, set sensor delays

            accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            gyroscope = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).get(0);

            if(!sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)){
                // SensorManager.SENSOR_DELAY_NORMAL
                textViewStatus.setText("Couldn't register accelerometer sensor listener");

            }else if(! sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI)){
                textViewStatus.setText("Couldn't register gyroscope sensor listener");

            }
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRecord = true;
                chronometerRecord.setBase(SystemClock.elapsedRealtime());
                chronometerRecord.start();
                textViewStatus.setText("Recording");
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRecord = false;
                chronometerRecord.stop();
                chronometerRecord.setBase(SystemClock.elapsedRealtime());

                logReadings(convertRecords());

                aRecord.clear();
                gRecord.clear();
            }
        });

    }

    private String convertRecords() {

        StringBuilder stringRecords = new StringBuilder();

        for(int i = 0; i < aRecord.size(); i++){
            if(i < gRecord.size()){
                stringRecords.append(aRecord.get(i).getAccelerometer(0));
                stringRecords.append("," + aRecord.get(i).getAccelerometer(1));
                stringRecords.append("," + aRecord.get(i).getAccelerometer(2));
                stringRecords.append("," + aRecord.get(i).getTime());
                stringRecords.append("," + gRecord.get(i).getGyroscope(0));
                stringRecords.append("," + gRecord.get(i).getGyroscope(1));
                stringRecords.append("," + gRecord.get(i).getGyroscope(2));
                stringRecords.append("," + gRecord.get(i).getTime() + "\n");
            }
        }

        return stringRecords.toString();
    }

    private void logReadings(String readings){
        try{
            File root = Environment.getExternalStorageDirectory();
            if(root.canWrite()){
                System.out.println("Can Write");
                java.util.Date date = new java.util.Date();
                Timestamp ts = new Timestamp(date.getTime());

                String datetime = new java.text.SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(ts);
                fileName = "WheelOnLog" + datetime + ".csv";
                File gpxfile = new File(root, fileName);
                FileWriter gpxWriter = new FileWriter(gpxfile);

                BufferedWriter out = new BufferedWriter(gpxWriter);
                out.write(readings);
                Toast.makeText(this,"Data is saved as " + fileName, Toast.LENGTH_SHORT).show();
                out.close();
            }else{
                Toast.makeText(this, "NO SD card Available", Toast.LENGTH_SHORT).show();
                System.out.println("Can NOT write");
            }
        } catch (IOException e) {
            System.out.println("could not log readings");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        java.util.Date date = new java.util.Date(); // for timestamp

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mAccl = event.values;

            textAcclX.setText("X: " + mAccl[0]);
            textAcclY.setText("Y: " + mAccl[1]);
            textAcclZ.setText("Z: " + mAccl[2]);


            if(buttonRecord == true && event.sensor.equals(accelerometer)){
                if(timestamp != date.getTime()){
                    timestamp = date.getTime();
                    aRecord.add(new Accelerometer(timestamp, mAccl));
                    aCount++;

                    if(aCount > gCount + 1){
                        for(int i = 0; i < 3; i++){
                            mGyro[i] = 0.0f;
                        }
                        gRecord.add(new Gyroscope(0, mGyro));
                        gCount++;
                    }
                }

            }
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){

            mGyro = event.values;

            textGyroX.setText("Pitch (X): " + mGyro[0]);
            textGyroY.setText("Roll (Y): " + mGyro[1]);
            textGyroZ.setText("Yaw (Z): " + mGyro[2]);


            if(buttonRecord == true && event.sensor.equals(gyroscope)){
                if(timestamp != date.getTime()){
                    timestamp = date.getTime();
                    gRecord.add(new Gyroscope(timestamp, mGyro));
                    gCount++;
                }
            }

        }
        
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
