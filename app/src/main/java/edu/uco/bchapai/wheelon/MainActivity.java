package edu.uco.bchapai.wheelon;

        import android.app.Activity;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.CountDownTimer;
        import android.os.Environment;
        import android.speech.tts.TextToSpeech;
        import android.view.View;
        import android.widget.Button;
        import android.widget.RadioButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.sql.Time;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.Locale;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView xAView, yAView, zAView, xGView, yGView, zGView;

    String fileName;
    private long timer;

    private Sensor accelerometer;
    private Sensor gyroscope;
    private SensorManager accelManager, gyroManager;
    private Button btnStart, btnStop, btnSave;
    private TextView num;

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
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnStop = (Button) findViewById(R.id.btn_stop);


        num = (TextView) findViewById(R.id.mynum);

        accelData = new ArrayList<Accelerometer>();
        gyroData = new ArrayList<Gyroscope>();

        btnStop.setEnabled(false);
        btnSave.setEnabled(false);

        accelManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = accelManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        // assign textviews
        xAView = (TextView) findViewById(R.id.xA);
        yAView = (TextView) findViewById(R.id.yA);
        zAView = (TextView) findViewById(R.id.zG);

        gyroManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);


        xGView = (TextView) findViewById(R.id.xG);
        yGView = (TextView) findViewById(R.id.yG);
        zGView = (TextView) findViewById(R.id.zG);

        toSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status == TextToSpeech.SUCCESS){
                    result = toSpeech.setLanguage(Locale.US);
                }  else{
                    Toast.makeText(MainActivity.this, "speech not supported", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    public void startbutton(View view) throws InterruptedException {


        Toast.makeText(MainActivity.this, "Start Button is pressed ", Toast.LENGTH_LONG).show();
        text = "Start button pressed, Please wait 5 second to begin";
        Speak(text);

        Thread.sleep(5000);
        text = "You may now proceed to collect data.";
        Speak(text);


        btnStop.setEnabled(true);


        /////////////////START COLLECTING DATA FROM HERE /////////////////////////////////////////////////////
        // we need to create an array list here that we need to save the gyroscope and accelerometer data to

    }

    public void savebutton(View view){

        btnStart.setEnabled(true);
        // save the arraylist, as to expert on .cvs file.
        saveData(toString());

        text = "Your Data has been saved.";
        Speak(text);
        Toast.makeText(MainActivity.this, "Save Button is pressed ", Toast.LENGTH_LONG).show();

    }

    public void stopbutton(View view){

        hasStarted = true;
        btnSave.setEnabled(true);
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);

        // stop pushing data to arraylist.
        text = "Stop button pressed, Please save your data.";
        Speak(text);
        Toast.makeText(MainActivity.this, "Stop Button is pressed ", Toast.LENGTH_LONG).show();

    }


    // this method takes text as an argument and speaks out of device.
    public void Speak(String texttospeak){

        if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
            Toast.makeText(getApplicationContext(),"Feature not supported om device", Toast.LENGTH_SHORT).show();
        }
        else
        {
            toSpeech.speak(texttospeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        date = new Date();

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            accel = event.values;



            xAView.setText("X: " + accel[0]);
            yAView.setText("Y: " + accel[1]);
            zAView.setText("Z: " + accel[2]);

            accelData.add(new Accelerometer(accel[0],accel[1],accel[2],timer));

            System.out.println("xa = " + accel[0] + "ya = " +  accel[1] + "za = " +  accel[2] + "time = " + timer);
        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){

            gyro = event.values;

            xGView.setText("X: " + gyro[0]);
            yGView.setText("Y: " + gyro[1]);
            zGView.setText("Z: " + gyro[2]);

            gyroData.add(new Gyroscope(gyro[0],gyro[1],gyro[2],timer));

        }
        System.out.println("xG = " + gyro[0] + "yG = " +  gyro [1] + "zG = " + gyro [2] + "time = " + timer + "/n");

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

