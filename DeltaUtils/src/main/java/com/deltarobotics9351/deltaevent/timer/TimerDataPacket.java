/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.timer;

public class TimerDataPacket {

    public long msEventTime = 0;
    public long msElapsedTime = 0;
    public long msLastSystemTime = 0;
    public long msStartSystemTime = 0;

    public boolean repeat = false;

    public TimerDataPacket(long msEventTime, long msElapsedTime, boolean repeat){
        this.msElapsedTime = msElapsedTime;
        this.msEventTime = msEventTime;
        this.repeat = repeat;
    }

    public TimerDataPacket(TimerDataPacket o){
        msEventTime = o.msEventTime;
        msElapsedTime = o.msElapsedTime;
        msLastSystemTime = o.msLastSystemTime;
        msStartSystemTime = o.msStartSystemTime;
    }

}
