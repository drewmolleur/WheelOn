package edu.uco.bchapai.wheelon;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{


    private TextView xAView,yAView,zAView,xGView,yGView,zGView;

    private Sensor accelerometer;
    private Sensor gyroscope;

    private SensorManager accelManager,gyroManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        xAView = (TextView)findViewById(R.id.xA);
        yAView = (TextView)findViewById(R.id.yA);
        zAView = (TextView)findViewById(R.id.zA);

        xGView = (TextView)findViewById(R.id.xG);
        yGView = (TextView)findViewById(R.id.yG);
        zGView = (TextView)findViewById(R.id.zG);



        accelManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = accelManager.getDefaultSensor((Sensor.TYPE_ACCELEROMETER));
        accelManager.registerListener((SensorEventListener) this,accelerometer,SensorManager.SENSOR_DELAY_UI);
        if (accelerometer == null) {
            xAView.setText("No Accelerometer found on this device.");
        }

        gyroManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroscope = gyroManager.getDefaultSensor((Sensor.TYPE_GYROSCOPE));
        gyroManager.registerListener((SensorEventListener) this,gyroscope,SensorManager.SENSOR_DELAY_UI);
        if (gyroscope == null) {
            xGView.setText("No Gyroscope found on this device.");
        }





    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            xAView.setText("X: " + event.values[0]);
            yAView.setText("Y: " + event.values[1]);
            zAView.setText("Z: " + event.values[2]);

        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            xGView.setText("X: " + event.values[0]);
            yGView.setText("Y: " + event.values[1]);
            zGView.setText("Z: " + event.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public String toString(){
        StringBuilder sb = new StringBuilder();



        return sb.toString();
    }




}
