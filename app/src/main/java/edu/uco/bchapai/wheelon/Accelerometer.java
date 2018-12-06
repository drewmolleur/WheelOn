package edu.uco.bchapai.wheelon;

/**
 * Created by Biwash on 10/12/2018.
 */

public class Accelerometer {

    private long time;
    private float[] accelerometer;

    
    public Accelerometer(){
        this.accelerometer = new float[3];
    }

    public Accelerometer(long time, float[] accelerometer){
        this.accelerometer = new float[3];
        this.time = new Long(time);
        this.accelerometer = accelerometer.clone();
    }

    public Accelerometer(long time, float xA, float yA, float zA){
        this.time = time;
        this.accelerometer = new float[3];
        this.accelerometer[0] = xA;
        this.accelerometer[1] = yA;
        this.accelerometer[2] = zA;
        
    }
    protected float[] getAccelerometer(){
        return this.accelerometer;
    }

    protected void setAccelerometer(float[] accelerometer){
        this.accelerometer = accelerometer;
    }

    protected float getAccelerometer(int index){
        return this.accelerometer[index];
    }

    protected void setTime(long time){
        this.time = time;
    }

    protected long getTime(){
        return this.time;
    }
    
}
