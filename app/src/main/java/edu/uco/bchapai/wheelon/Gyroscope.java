package edu.uco.bchapai.wheelon;

/**
 * Created by Biwash on 10/12/2018.
 */

public class Gyroscope {

    private float[] gyroscope;
    private long time;
    
    public Gyroscope(){
        this.gyroscope = new float[3];
    }
    public Gyroscope (long time, float[] gyroscope){
        this.gyroscope = new float[3];
        this.time = new Long(time);
        this.gyroscope = gyroscope.clone();
    }

    public Gyroscope (long time, float x, float y, float z){
        this.gyroscope = new float[3];
        this.time = time;
        this.gyroscope[0] = x;
        this.gyroscope[1] = y;
        this.gyroscope[2] = z;
    }
    protected float[] getGyroscope(){
        return gyroscope;
    }

    protected void setGyroscope (float[] gyroscope){
        this.gyroscope = gyroscope;
    }

    protected float getGyroscope(int index){
        return this.gyroscope[index];
    }
    protected long getTime(){
        return time;
    }
    protected void setTime (long timeStamp){
        this.time = time;
    }
    
}
