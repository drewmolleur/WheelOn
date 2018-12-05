package edu.uco.bchapai.wheelon;

/**
 * Created by Biwash on 11/12/2018.
 */

public class Gyroscope {

    private float[] gyroscope;
    private long time;

    public Gyroscope(){
        this.gyroscope = new float[3];
    }

    public Gyroscope(float[] gyroscope, long time){
        this.gyroscope = gyroscope;
        this.time = time;
    }

    public Gyroscope(float x, float y, float z, long t){
        this.gyroscope = new float[3];
        this.gyroscope[0] = x;
        this.gyroscope[1] = y;
        this.gyroscope[2] = z;
        this.time = t;


    }

    private float getGyroscope(int i){
        return this.gyroscope[i];
    }

    private float[] getGyroscope(){
        return gyroscope;
    }

    private void setGyroscope(float[] gyroscope){
        this.gyroscope = gyroscope;
    }

    private long getTime(){
        return time;
    }

    private void setTime(long t){
        this.time = t;
    }
}