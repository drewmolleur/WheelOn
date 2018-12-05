package edu.uco.bchapai.wheelon;

/**
 * Created by Biwash on 11/12/2018.
 */

public class Accelerometer {

    private float[] accelerometer;
    private long time;

    public Accelerometer(){
        this.accelerometer = new float[3];
    }

    public Accelerometer(float[] accelerometer, long time){
        this.accelerometer = accelerometer;
        this.time = time;
    }

    public Accelerometer(float x, float y, float z, long t){
        this.accelerometer = new float[3];
        this.accelerometer[0] = x;
        this.accelerometer[1] = y;
        this.accelerometer[2] = z;
        this.time = t;


    }

    private float getAccelerometer(int i){
        return this.accelerometer[i];
    }

    private float[] getAccelerometer(){
        return accelerometer;
    }

    private void setAccelerometer(float[] accelerometer){
        this.accelerometer = accelerometer;
    }

    private long getTime(){
        return time;
    }

    private void setTime(long t){
        this.time = t;
    }
}
