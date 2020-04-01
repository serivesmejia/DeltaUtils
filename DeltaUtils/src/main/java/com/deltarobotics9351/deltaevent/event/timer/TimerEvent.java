/*
 * Created by FTC team Delta Robotics #9351
 *  Source code licensed under the MIT License
 *  More info at https://choosealicense.com/licenses/mit/
 */

package com.deltarobotics9351.deltaevent.event.timer;

import com.deltarobotics9351.deltaevent.event.Event;
import com.deltarobotics9351.deltaevent.timer.TimerDataPacket;

import java.util.ArrayList;
import java.util.HashMap;

public class TimerEvent extends Event {

    private boolean cancelled = false;

    @Override
    public final void execute(Object arg1, Object arg2) {
        throw new UnsupportedOperationException("This method is not supported in TimerEvent");
    }

    @Override
    public final void execute(Object arg1) {
        throw new UnsupportedOperationException("This method is not supported in TimerEvent");
    }

    @Override
    public final void execute(ArrayList<Object> args) {
        throw new UnsupportedOperationException("This method is not supported in TimerEvent");
    }

    @Override
    public final void execute(HashMap<Object, Object> args) {
        throw new UnsupportedOperationException("This method is not supported in TimerEvent");
    }

    public final void cancel(){
       cancelled = true;
       cancelEvent();
    }

    public final boolean isCancelled(){
        return cancelled;
    }

    public void startEvent(){ }

    public void timeoutEvent(){ }

    public void cancelEvent(){ }

    public void loopEvent(TimerDataPacket evtTime){ }

}
