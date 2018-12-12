package edu.uco.bchapai.wheelon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends Activity implements SensorEventListener{
    
    private ArrayList<Accelerometer> accelData;
    private ArrayList<Gyroscope> gyroData;
    float accelValue[] = new float[3];
    float gyroValue[] = new float[3];
    private long timestamp;

    public FTPClient mFTPClient = null;

    String result;

    private Sensor accelerometer, gyroscope;
    private int accelCounter, gyroCounter;

    Button btnStart,btnAnalyze,btnStop;

    private String fileName;
    private SensorManager sensorManager; 

    private boolean buttonRecord = false; 

    private TextView xAText, yAText, zAText, xGText, yGText, zGText, textViewStatus;
    private Chronometer chronometer;

    Date date;

    StringBuilder stringData;

    private  static final String TAG = "MyFTPClientFunctions";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnStart = (Button) findViewById(R.id.startbtn);
        btnAnalyze = (Button) findViewById(R.id.analyzebtn);
        btnStop = (Button) findViewById(R.id.endbtn);

        accelData = new ArrayList<Accelerometer>();
        gyroData = new ArrayList<Gyroscope>();
        accelCounter = 0;
        gyroCounter = 0;

        xAText = (TextView) findViewById(R.id.xA);
        yAText = (TextView) findViewById(R.id.yA);
        zAText = (TextView) findViewById(R.id.zA);
        xGText = (TextView) findViewById(R.id.xG);
        yGText = (TextView) findViewById(R.id.yG);
        zGText = (TextView) findViewById(R.id.zG);

        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        chronometer = findViewById(R.id.chrono);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRecord = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                textViewStatus.setText("Recording On Progress.");
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRecord = false;
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());

                btnAnalyze.setVisibility(View.VISIBLE);
                btnStart.setEnabled(false);

                logReadings(convertRecords());

                accelData.clear();
                gyroData.clear();
            }
        });

        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://wheelon.azurewebsites.net"));
                startActivity(browserIntent);
            }
        });

    }

    public String convertRecords() {

        stringData = new StringBuilder();

        float xInitial,yInitial,zInitial;
        xInitial = accelData.get(0).getAccelerometer(0);
        yInitial = accelData.get(0).getAccelerometer(1);
        zInitial = accelData.get(0).getAccelerometer(2);
        for(int i = 0; i < accelData.size(); i++){
//                if(accelData.get(i).getAccelerometer(0) == 0.0 ){
//                    if(accelData.get(i).getAccelerometer(1) == 9.81){
//                        if(accelData.get(i).getAccelerometer(2) == 0.0){
//                            result = "Stationary";
//                        }
//                    }
//                }
                if(accelData.get(i).getAccelerometer(0) == xInitial){

                    if(accelData.get(i).getAccelerometer(1) == yInitial){

                        if(accelData.get(i).getAccelerometer(2) == zInitial){
                            result = "Stationary";
                            xInitial = accelData.get(i).getAccelerometer(0);
                            yInitial = accelData.get(i).getAccelerometer(1);
                            zInitial = accelData.get(i).getAccelerometer(2);
                        }
                    }
                }

                else{
                    result = "Moving";
                }
            if(i < gyroData.size()){
                stringData.append(accelData.get(i).getAccelerometer(0));
                stringData.append("," + accelData.get(i).getAccelerometer(1));
                stringData.append("," + accelData.get(i).getAccelerometer(2));
                stringData.append("," + accelData.get(i).getTime());
                stringData.append("," + gyroData.get(i).getGyroscope(0));
                stringData.append("," + gyroData.get(i).getGyroscope(1));
                stringData.append("," + gyroData.get(i).getGyroscope(2));
                stringData.append("," + gyroData.get(i).getTime());
                stringData.append("," + result + "\n");
            }
        }

        return stringData.toString();

    }

    public void logReadings(String readings){
        try{
            File root = new File(Environment.getExternalStorageDirectory(),
                    "TAGFtp");
            if(root.canWrite()){
                System.out.println("Can Write");
                date = new Date();
                Timestamp ts = new Timestamp(date.getTime());

                String datetime = new java.text.SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(ts);
                fileName = "mostrecent.csv";
                File gpxfile = new File(root, fileName);

                System.out.println(gpxfile.toString());

                String file = fileName.toString();



                FileWriter gpxWriter = new FileWriter(gpxfile);

                BufferedWriter out = new BufferedWriter(gpxWriter);
                out.write(readings);
                Toast.makeText(this,"Data is Saved As:  " + fileName, Toast.LENGTH_LONG).show();
                out.close();


                Intent intent1 = new Intent(this, MainActivity.class);
                intent1.putExtra("FILE",file);
                startActivity(intent1);

            }else{
                Toast.makeText(this, "SD Card Unavailable.", Toast.LENGTH_SHORT).show();
                System.out.println("Can NOT write");
            }
        } catch (IOException e) {
            System.out.println("Error, Saving Data.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

         date = new Date(); 

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelValue = event.values;

            xAText.setText("X: " + accelValue[0]);
            yAText.setText("Y: " + accelValue[1]);
            zAText.setText("Z: " + accelValue[2]);


            if(buttonRecord == true || event.sensor.equals(accelerometer)){
                if(timestamp != date.getTime()){
                    timestamp = date.getTime();
                    accelData.add(new Accelerometer(timestamp, accelValue));
                    accelCounter++;

                    if(accelCounter > gyroCounter + 1){
                        for(int i = 0; i < 3; i++){
                            gyroValue[i] = 0.0f;
                        }
                        gyroData.add(new Gyroscope(0, gyroValue));
                        gyroCounter++;
                    }
                }

            }
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){

            gyroValue = event.values;

            xGText.setText("X: " + gyroValue[0]);
            yGText.setText("Y: " + gyroValue[1]);
            zGText.setText("Z" +
                    ": " + gyroValue[2]);


            if(buttonRecord == true || event.sensor.equals(gyroscope)){
                if(timestamp != date.getTime()){
                    timestamp = date.getTime();
                    gyroData.add(new Gyroscope(timestamp, gyroValue));
                    gyroCounter++;
                }
            }

        }
        
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
